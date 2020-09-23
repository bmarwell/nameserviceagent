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

import static io.github.bmhm.nameserviceagent.agent.nameservice.ProxyIpv4Helper.GOOGLE_IP;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.bmhm.nameserviceagent.api.NameService;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

class DefaultSequentialReachableNameServiceTest {

  public static final String GOOGLE_COM_DOMAIN = "google.com";

  @Test
  void testPassThroughGetHostByAddr() throws UnknownHostException {
    // given
    final NameService original = mock(NameService.class);
    final DefaultSequentialReachableNameService defaultSequentialReachableNameService = new DefaultSequentialReachableNameService(original);

    // when
    defaultSequentialReachableNameService.getHostByAddr(GOOGLE_IP);

    // then
    verify(original).getHostByAddr(GOOGLE_IP);
  }

  @Test
  void testTryReachable() throws IOException {
    // given
    final NameService original = mock(NameService.class);
    final DefaultSequentialReachableNameService defaultSequentialReachableNameService = new DefaultSequentialReachableNameService(original);
    final InetAddress fakeInetAddress = ProxyIpv4Helper.proxyGoogleIpv4();

    // when
    when(original.lookupAllHostAddr(GOOGLE_COM_DOMAIN)).then(args -> new InetAddress[] {fakeInetAddress});
    final InetAddress[] inetAddresses = defaultSequentialReachableNameService.lookupAllHostAddr(GOOGLE_COM_DOMAIN);

    // then
    assertTrue(inetAddresses.length == 1);
    verify(original).lookupAllHostAddr(GOOGLE_COM_DOMAIN);
    verify(fakeInetAddress).isReachable(anyInt());

  }

}
