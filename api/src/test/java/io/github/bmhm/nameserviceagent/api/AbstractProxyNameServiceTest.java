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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AbstractProxyNameServiceTest {

  @Test
  @DisplayName("generated exception should be UnsupportedOperationException with generated signature")
  void testExceptionMessage() throws Exception {
    // given
    // -- unfinished instance
    final AbstractProxyNameService nameService = mock(AbstractProxyNameService.class);
    when(nameService.createException(any(Method.class))).thenCallRealMethod();
    // extended nameservice
    final Method noopMethod = ExtendedNameService.class.getDeclaredMethod("noop", String.class);

    // when
    final UnsupportedOperationException nameServiceException = nameService.createException(noopMethod);

    // then
    assertEquals("java.lang.UnsupportedOperationException: void noop(java.lang.String p0)", nameServiceException.toString());
  }

  @Test
  @DisplayName("invocation with unknonwn method should throw UnsupportedOperationException")
  void testInvocationThrowsOnUnknownMethod() throws Throwable {
    // given
    // -- unfinished instance
    final AbstractProxyNameService nameService = mock(AbstractProxyNameService.class);
    when(nameService.createException(any(Method.class))).thenCallRealMethod();
    when(nameService.invoke(any(Object.class), any(Method.class), any(Object[].class))).thenCallRealMethod();
    // -- wrong method
    final Method method = InetAddress.class.getMethod("getAddress");

    // expect exception
    final UnsupportedOperationException illegalArgumentException =
        assertThrows(UnsupportedOperationException.class, () -> nameService.invoke(mock(Object.class), method, new Object[0]));

    // then assert
    assertThat(illegalArgumentException.getMessage(), containsString("getAddress"));
  }

  @Test
  @DisplayName("default constructor provides original name service")
  void testDefaultConstructorProvidesOriginalNameService() throws Throwable {
    // given
    final NameService originalNameService = mock(NameService.class);
    final DelegatingNameService noOpNameService = new DelegatingNameService(originalNameService);
    final String hostname = "localhost";
    final byte[] addr = new byte[0];

    // when
    noOpNameService.lookupAllHostAddr(hostname);
    noOpNameService.getHostByAddr(addr);

    // then
    // verify mocked original was called
    verify(originalNameService).lookupAllHostAddr(hostname);
    verify(originalNameService).getHostByAddr(addr);
  }

  interface ExtendedNameService extends NameService {
    default void noop(final String ignored) {
      // noop
    }
  }

  static class DelegatingNameService extends AbstractProxyNameService {

    public DelegatingNameService(final NameService originalNameService) {
      super(originalNameService);
    }

    @Override
    public InetAddress[] lookupAllHostAddr(final String host) throws UnknownHostException {
      return this.getOriginalNameService().lookupAllHostAddr(host);
    }

    @Override
    public String getHostByAddr(final byte[] addr) throws UnknownHostException {
      return this.getOriginalNameService().getHostByAddr(addr);
    }
  }

}
