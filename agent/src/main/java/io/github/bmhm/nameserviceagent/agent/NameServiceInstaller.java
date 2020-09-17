/*
 * Copyright 2020-2020 the nameserviceangent team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.bmhm.nameserviceagent.agent;

import static io.github.bmhm.nameserviceagent.agent.util.TryingReflectionLoader.loadFirstAvailableClass;
import static java.util.Collections.singletonList;

import io.github.bmhm.nameserviceagent.agent.util.TryingReflectionLoader;
import io.github.bmhm.nameserviceagent.api.AbstractProxyNameService;
import io.github.bmhm.nameserviceagent.api.ForbiddenApiCall;
import io.github.bmhm.nameserviceagent.api.NameService;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

public final class NameServiceInstaller {

  private NameServiceInstaller() {
    // util class
  }

  public static void install(final Class<? extends AbstractProxyNameService> replacementClass)
      throws SecurityException, ReflectiveOperationException {
    final Field nameServiceField = determineOriginalNameServiceField();
    final Object originalNameService = nameServiceField.get(null);
    final AbstractProxyNameService customNameService = instantiateCustomNameService(replacementClass, originalNameService);

    final Object customNameServiceProxy;

    if (nameServiceField.getName().endsWith("s")) {
      // the definition for e.g. openj9@1.8:
      // private static List<NameService> nameServices
      customNameServiceProxy = singletonList(proxyCustomService(customNameService));
    } else {
      // the definition for e.g. adopt@1.11 and adopt-openj9@1.11:
      // private static transient NameService nameService
      customNameServiceProxy = proxyCustomService(customNameService);

    }

    installProxy(nameServiceField, customNameServiceProxy);
  }

  @ForbiddenApiCall(justification = "agent needs to set")
  protected static void installProxy(final Field nameServiceField, final Object customNameServiceProxy) throws IllegalAccessException {
    // if this fails in java 9+ (and it will), we can use something like this:
    // https://blog.gotofinal.com/java/breakingjava/2017/11/08/reflections-in-java-9.html
    nameServiceField.setAccessible(true);
    nameServiceField.set(InetAddress.class, customNameServiceProxy);
  }

  /**
   * The just created custom service is now proxied into the interface used by java internally.
   *
   * @param createdNameService
   * @return
   */
  protected static Object proxyCustomService(final AbstractProxyNameService createdNameService) {
    final Class<?> proxiedInterface = determineProxiedInterface();

    return Proxy.newProxyInstance(
        proxiedInterface.getClassLoader(),
        new Class<?>[] {proxiedInterface},
        createdNameService
    );
  }

  protected static Class<?> determineProxiedInterface() {
    final String[] classes = {
        "java.net.InetAddress$NameService",
        "sun.net.spi.nameservice.NameService"
    };

    return loadFirstAvailableClass(classes)
        .orElseThrow(() -> new IllegalArgumentException("Cannot load any of these classes: " + Arrays.toString(classes)));
  }

  @ForbiddenApiCall(justification = "agent needs to read field")
  protected static Field determineOriginalNameServiceField() {
    final String[] fieldNames = {"nameService", "nameServices"};

    final Field field = TryingReflectionLoader.loadFirstAvailableField(InetAddress.class, fieldNames)
        .orElseThrow(() -> new IllegalArgumentException("Cannot load either field: [" + Arrays.toString(fieldNames) + "]."));
    field.setAccessible(true);

    return field;
  }

  /**
   * Instanciates an {@link AbstractProxyNameService} from the given class reference.
   *
   * <p>This is just a wrapper over the 'newInstance(args)' method, where args is the proxied instance of the original service.</p>
   *
   * @param replacementClass    the class reference to be instantiated.
   * @param originalNameService the original services to be given to the constructor.
   * @return the instantiated {@link AbstractProxyNameService}.
   * @throws ReflectiveOperationException if java forbids to do so.
   */
  protected static AbstractProxyNameService instantiateCustomNameService(final Class<? extends AbstractProxyNameService> replacementClass,
                                                                         final Object originalNameService)
      throws ReflectiveOperationException {
    final NameService proxiedOriginalNameService = proxyOriginalNameService(originalNameService);

    return replacementClass.getConstructor(NameService.class).newInstance(proxiedOriginalNameService);
  }

  protected static NameService proxyOriginalNameService(final Object originalNameService) {
    Object actualOriginalNameService = originalNameService;
    if (originalNameService instanceof List) {
      actualOriginalNameService = ((List<?>) originalNameService).get(0);
    }
    try {
      final Method lookupAllHostAddrMethod = actualOriginalNameService.getClass().getDeclaredMethod("lookupAllHostAddr", String.class);
      final Method getHostByAddrMethod = actualOriginalNameService.getClass().getDeclaredMethod("getHostByAddr", byte[].class);
    } catch (final NoSuchMethodException noSuchMethodException) {
      // invalid
      throw new IllegalArgumentException(
          "originalNameService does not implement methods: " + noSuchMethodException.getMessage(),
          noSuchMethodException);
    }

    final Object proxyInstance =
        Proxy.newProxyInstance(NameServiceInstaller.class.getClassLoader(), new Class<?>[] {NameService.class},
            new OriginalNameServiceProxy(actualOriginalNameService));

    return (NameService) proxyInstance;
  }

}
