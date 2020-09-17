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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.github.bmhm.nameserviceagent.api.AbstractProxyNameService;
import io.github.bmhm.nameserviceagent.api.NameService;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.net.UnknownHostException;

class NameServiceInstallerTest {

  @Test
  @DisplayName("Assert that any proxied interface could be found")
  void testDetermineProxiedInterface() {
    // given and when
    final Class<?> aClass = NameServiceInstaller.determineProxiedInterface();

    // then
    assertNotNull(aClass);
  }

  @Test
  void testDetermineOriginalNameServiceField() {
    final Field field = NameServiceInstaller.determineOriginalNameServiceField();

    assertThat(
        field.getName(),
        Matchers.anyOf(
            is("nameService"),
            is("nameServices")
        )
    );
  }

  @Test
  void testProxyProducesProxy() {
    final NameService originalNameService = mock(NameService.class);
    final NameService nameService = NameServiceInstaller.proxyOriginalNameService(originalNameService);

    assertTrue(Proxy.isProxyClass(nameService.getClass()));
  }

  @Test
  void testCustomProxyProducesProxy() {
    final AbstractProxyNameService invocationHandler = mock(AbstractProxyNameService.class);
    final Object nameServiceProxy = NameServiceInstaller.proxyCustomService(invocationHandler);

    assertTrue(Proxy.isProxyClass(nameServiceProxy.getClass()));
  }

  /**
   * This methods gets a class name  which was already loaded by "forClass".
   */
  @Test
  void testCreateCustomService() throws ReflectiveOperationException {
    final NameService originalMock = mock(NameService.class);

    // when
    final AbstractProxyNameService instantiatedService =
        NameServiceInstaller.instantiateCustomNameService(NoopMock.class, originalMock);

    // then
    assertTrue(Proxy.isProxyClass(instantiatedService.getOriginalNameService().getClass()));
  }

  static class NoopMock extends AbstractProxyNameService {

    public NoopMock(final NameService originalNameService) {
      super(originalNameService);
    }

    @Override
    public InetAddress[] lookupAllHostAddr(final String host) throws UnknownHostException {
      throw new UnsupportedOperationException(
          "not yet implemented: [io.github.bmhm.nameserviceagent.agent.NameServiceInstallerTest.NoopMock::lookupAllHostAddr].");
    }

    @Override
    public String getHostByAddr(final byte[] addr) throws UnknownHostException {
      throw new UnsupportedOperationException(
          "not yet implemented: [io.github.bmhm.nameserviceagent.agent.NameServiceInstallerTest.NoopMock::getHostByAddr].");
    }
  }
}
