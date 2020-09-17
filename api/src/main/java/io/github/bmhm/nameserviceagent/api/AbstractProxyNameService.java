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

package io.github.bmhm.nameserviceagent.api;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.StringJoiner;

/**
 * The agent will only accept extensions of this class.
 */
public abstract class AbstractProxyNameService implements NameService, InvocationHandler {

  /**
   * The original name service. Probably system’s DNS or host file.
   */
  private final NameService originalNameService;

  /**
   * The original name service will get injected.
   *
   * @param originalNameService the original name service before proxying.
   */
  public AbstractProxyNameService(final NameService originalNameService) {
    this.originalNameService = originalNameService;
  }

  /**
   * Invokes another method. In this case, just invoke the methods supplied in this method
   * instead of those from the proxy object (which would cause a loop).
   * <p>
   * {@inheritDoc}
   */
  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    switch (method.getName()) {
      case "lookupAllHostAddr":
        return this.lookupAllHostAddr((String) args[0]);
      case "getHostByAddr":
        return this.getHostByAddr((byte[]) args[0]);
      default:
        throw this.createException(method);

    }
  }

  /**
   * Creates an {@link UnsupportedOperationException} which will be thrown in the unprobably case that someone extends this class
   * and overwrites the {@link #invoke(Object, Method, Object[])} method incorrectly.
   *
   * <p>The exception will look like this (toString output):
   * {@code java.lang.UnsupportedOperationException: void noop(java.lang.String p0)}.</p>
   *
   * @param method the method which should have been called but did not exist in the {@link #invoke(Object, Method, Object[])} method.
   * @return an exception which can be thrown.
   */
  protected UnsupportedOperationException createException(final Method method) {
    final StringBuilder other = new StringBuilder();
    other.append(method.getReturnType().getCanonicalName()).append(" ").append(method.getName()).append("(");
    final Class<?>[] ps = method.getParameterTypes();

    for (int parameterTypeCounter = 0; parameterTypeCounter < ps.length; ++parameterTypeCounter) {
      if (parameterTypeCounter > 0) {
        other.append(", ");
      }
      other.append(ps[parameterTypeCounter].getCanonicalName()).append(" p").append(parameterTypeCounter);
    }

    other.append(")");

    return new UnsupportedOperationException(other.toString());
  }

  /**
   * Returns the original name service. Useful if you want to extend the original name service or modify it’s behaviour instead
   * of implementing your own lookup.
   *
   * @return the original nameservice.
   */
  public NameService getOriginalNameService() {
    return this.originalNameService;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", AbstractProxyNameService.class.getSimpleName() + "[", "]")
        .add("super=" + super.toString())
        .add("originalNameService=" + this.originalNameService)
        .toString();
  }
}
