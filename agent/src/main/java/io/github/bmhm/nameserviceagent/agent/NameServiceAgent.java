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

import io.github.bmhm.nameserviceagent.agent.nameservice.DefaultSequentialRetryingNameService;
import io.github.bmhm.nameserviceagent.api.AbstractProxyNameService;

import java.lang.instrument.Instrumentation;

/**
 * NameService agent.
 */
public final class NameServiceAgent {

  private static final String DEFAULT_CLASS_NAME = DefaultSequentialRetryingNameService.class.getCanonicalName();

  private NameServiceAgent() {
    // util
  }

  public static void premain(final String agentArgs, final Instrumentation inst) throws Exception {
    installAgent();
  }

  public static void agentmain(final String agentArgs, final Instrumentation inst) throws Exception {
    installAgent();
  }

  private static void installAgent() throws ReflectiveOperationException {
    final Class<? extends AbstractProxyNameService> nameService = loadCustomNameServiceClass();
    NameServiceInstaller.install(nameService);
  }

  protected static Class<? extends AbstractProxyNameService> loadCustomNameServiceClass() throws ClassNotFoundException {
    final String className = getNameServiceClassName();

    return doLoadNameServiceReplacementClass(className);
  }

  protected static String getNameServiceClassName() {
    return System.getProperty(
        "nameserviceagent.implementation",
        DEFAULT_CLASS_NAME
    );
  }

  /**
   * Loads the class and checks if it implements {@link AbstractProxyNameService}.
   *
   * @param className the class name to load.
   * @return the loaded class.
   * @throws ClassNotFoundException   if the class was not found or is not visible for this agent.
   * @throws IllegalArgumentException if className’s class is not assignable from {@link AbstractProxyNameService}.
   */
  protected static Class<? extends AbstractProxyNameService> doLoadNameServiceReplacementClass(final String className)
      throws ClassNotFoundException {
    final Class<?> nameServiceClass = Class.forName(className);
    if (!AbstractProxyNameService.class.isAssignableFrom(nameServiceClass)) {
      throw new IllegalArgumentException(
          "Class [" + className + "] is not an instance of [" + AbstractProxyNameService.class.getCanonicalName() + "]!"
      );
    }

    //noinspection unchecked
    return (Class<? extends AbstractProxyNameService>) nameServiceClass;
  }
}
