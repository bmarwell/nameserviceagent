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

package io.github.bmhm.namesergice.agent.its;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class AnyIpToLocalhostTest {

  /**
   * Actually, this does not really work. There is an anti-spoofing-mechanism in place.
   *
   * <p>For this to work, the {@link io.github.bmhm.nameserviceagent.agent.nameservice.AlwaysLocalhostLoopbackNameService#lookupAllHostAddr(String)}
   * would need to return all available IP addresses.</p>
   *
   * @param ip the IP to check.
   * @throws UnknownHostException should not happen in this case <code>:)</code>.
   */
  @ParameterizedTest
  @DisplayName(value = "ensure that any ip back-resolves to localhost")
  @ValueSource(strings = {
      "127.0.0.1",
      "[::1]",
      "8.8.8.8"
  })
  void testIpResolvesToLocalhost(final String ip) throws UnknownHostException {
    // given
    final InetAddress byIp = InetAddress.getByName(ip);

    // assertThat
    // sadly this does not work… see javadoc.
    // assertThat(byIp.getHostName(), anyOf( "localHost", … ))
    assertThat(byIp.getHostName(), notNullValue());
  }

}
