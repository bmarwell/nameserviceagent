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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.bmhm.nameserviceagent.agent.nameservice.DefaultSequentialReachableNameService;
import io.github.bmhm.nameserviceagent.api.AbstractProxyNameService;
import io.github.bmhm.nameserviceagent.api.NameService;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

class NameServiceAgentTest {

  @Test
  void testLoadNameServiceClassThrowsException() {
    final ClassNotFoundException notFoundException = assertThrows(
        ClassNotFoundException.class,
        () -> NameServiceAgent.doLoadNameServiceReplacementClass("not_existing_class$doesnt_exist")
    );

    assertThat(notFoundException.getMessage(), containsString("not_existing_class$doesnt_exist"));
  }

  @Test
  void testRejectsWrongClasses() {
    final IllegalArgumentException illegalArgumentException = assertThrows(
        IllegalArgumentException.class,
        () -> NameServiceAgent.doLoadNameServiceReplacementClass(String.class.getName())
    );

    assertThat(illegalArgumentException.getMessage(), containsString("is not an instance of"));
    assertThat(illegalArgumentException.getMessage(), containsString(AbstractProxyNameService.class.getSimpleName()));
  }

  @Test
  void testLoadNameServiceClass() throws Exception {
    // given
    final String className = NoopNameService.class.getName();

    // when
    final Class<? extends AbstractProxyNameService> replacementClass = NameServiceAgent.doLoadNameServiceReplacementClass(className);

    // then
    assertThat(replacementClass.getName(), is(className));
  }

  @Test
  void testNameServiceClassName() {
    final String nameServiceClassName = NameServiceAgent.getNameServiceClassName();

    assertEquals(DefaultSequentialReachableNameService.class.getName(), nameServiceClassName);
  }

  @Test
  void testLoadDefaultServiceClass() throws ClassNotFoundException {
    final Class<? extends AbstractProxyNameService> serviceClass = NameServiceAgent.loadCustomNameServiceClass();

    assertThat(serviceClass.getName(), is(DefaultSequentialReachableNameService.class.getName()));
  }

  static class NoopNameService extends AbstractProxyNameService {

    /**
     * The original name service will get injected.
     *
     * @param originalNameService the original name service before proxying.
     */
    public NoopNameService(final NameService originalNameService) {
      super(originalNameService);
    }

    @Override
    public InetAddress[] lookupAllHostAddr(final String host) throws UnknownHostException {
      throw new UnsupportedOperationException(
          "not yet implemented: [io.github.bmhm.nameserviceagent.agent.NameServiceAgentTest.NoopNameService::lookupAllHostAddr].");
    }

    @Override
    public String getHostByAddr(final byte[] addr) throws UnknownHostException {
      throw new UnsupportedOperationException(
          "not yet implemented: [io.github.bmhm.nameserviceagent.agent.NameServiceAgentTest.NoopNameService::getHostByAddr].");
    }
  }

}
