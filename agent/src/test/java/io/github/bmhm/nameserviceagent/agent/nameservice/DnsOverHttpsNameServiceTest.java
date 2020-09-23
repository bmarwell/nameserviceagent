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

package io.github.bmhm.nameserviceagent.agent.nameservice;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

class DnsOverHttpsNameServiceTest {

  private final DnsOverHttpsNameService dnsService = new DnsOverHttpsNameService(null);

  @Test
  void testResolveGitHub() throws UnknownHostException {
    // given
    final String hostname = "github.com";

    // when
    final InetAddress[] inetAddresses = this.dnsService.lookupAllHostAddr(hostname);

    // then
    assertTrue(inetAddresses.length != 0);
  }

  @Test
  void testResolveTwitter() throws UnknownHostException {
    // given
    final String hostname = "twitter.com";

    // when
    final InetAddress[] inetAddresses = this.dnsService.lookupAllHostAddr(hostname);

    // then
    assertTrue(inetAddresses.length != 0);
  }

  @Test
  void testResolveGoogle() throws UnknownHostException {
    // given
    final String hostname = "google.com";

    // when
    final InetAddress[] inetAddresses = this.dnsService.lookupAllHostAddr(hostname);

    // then
    assertTrue(inetAddresses.length != 0);
  }

}
