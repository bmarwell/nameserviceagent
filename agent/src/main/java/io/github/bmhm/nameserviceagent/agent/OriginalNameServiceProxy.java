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

import io.github.bmhm.nameserviceagent.api.ForbiddenApiCall;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Proxy class for the original nameService, which will have two known methods. But since we cannot reach its interface nor
 * now the interface’s name (it changes between Java releases), we need to proxy the method calls.
 *
 * <p>Luckily, as the method names never changed,
 * we can just proxy it with the same methods as {@link io.github.bmhm.nameserviceagent.api.NameService} provides.</p>
 */
public class OriginalNameServiceProxy implements InvocationHandler {

  private final Object actualOriginalNameService;

  public OriginalNameServiceProxy(final Object actualOriginalNameService) {
    this.actualOriginalNameService = actualOriginalNameService;
  }

  @Override
  @ForbiddenApiCall
  public Object invoke(final Object proxy, final Method method, final Object[] args) {
    final Class<?> originalNameServiceClass = this.actualOriginalNameService.getClass();

    try {
      switch (method.getName()) {
        case "lookupAllHostAddr":
          final Method originallookupAllHostAddr = originalNameServiceClass.getMethod(method.getName(), String.class);
          originallookupAllHostAddr.setAccessible(true);
          return originallookupAllHostAddr.invoke(this.actualOriginalNameService, (String) args[0]);
        case "getHostByAddr":
          final Method originalgetHostByAddr = originalNameServiceClass.getMethod(method.getName(), byte[].class);
          originalgetHostByAddr.setAccessible(true);
          //noinspection PrimitiveArrayArgumentToVarargsMethod
          return originalgetHostByAddr.invoke(this.actualOriginalNameService, (byte[]) args[0]);
        default:
          throw new IllegalArgumentException("method [" + method.getName() + "] does not exist or is not accessible.");
      }
    } catch (final ReflectiveOperationException noSuchMethodException) {
      throw new IllegalArgumentException("method [" + method.getName() + "] does not exist.", noSuchMethodException);
    }
  }
}
