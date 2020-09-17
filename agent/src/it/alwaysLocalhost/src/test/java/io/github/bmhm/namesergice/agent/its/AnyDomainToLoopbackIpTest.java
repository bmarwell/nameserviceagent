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
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class AnyDomainToLoopbackIpTest {

  @ParameterizedTest
  @DisplayName(value = "ensure that any domain name resolves to 127.0.0.1 or ::1")
  @ValueSource(strings = {
      "mydomain.com",
      "readme.invalid",
      "github.io",
      "localhost",
      "loopback"
  })
  void testDomainToLoopbackIp(final String domain) throws UnknownHostException {
    // given
    final InetAddress byName = InetAddress.getByName(domain);

    // assertThat
    // assertThat
    assertThat(byName.getAddress(),
        anyOf(
            is(new byte[] {127, 0, 0, 1}),
            is(new byte[] {127, 0, 0, 1}),
            is(Inet4Address.getLoopbackAddress().getAddress()),
            is(Inet6Address.getLoopbackAddress().getAddress())
        )
    );
  }
}
