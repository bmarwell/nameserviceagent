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
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.bmhm.nameserviceagent.api.NameService;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

class OriginalNameServiceProxyTest {

  private static final byte[] GOOGLE_DNS_IP = {8, 8, 8, 8};
  private static final String GOOGLE_DOMAIN = "google.com";

  /**
   * This proxy is not intended for extension unless the invoke method is being overridden again.
   *
   * <p>Test if foreign methods will fail.</p>
   */
  @Test
  void testProxyTakesMethods() throws Exception {
    final NameService originalNameServiceMock = this.getOriginalNameServiceMock();
    final Method lookupAllHostAddr = originalNameServiceMock.getClass().getMethod("lookupAllHostAddr", String.class);
    final OriginalNameServiceProxy originalNameServiceProxy = new OriginalNameServiceProxy(originalNameServiceMock);

    // this should work as the NameService interface should always implement the same methods as the original interface.
    final Object hostAddr = originalNameServiceProxy.invoke(originalNameServiceMock, lookupAllHostAddr, new Object[] {"google.com"});

    // then
    assertThat(hostAddr, instanceOf(InetAddress[].class));
    final InetAddress[] returnedHostAddr = (InetAddress[]) hostAddr;
    assertThat(returnedHostAddr[0].getAddress(), is(GOOGLE_DNS_IP));
    verify(originalNameServiceMock).lookupAllHostAddr(GOOGLE_DOMAIN);
  }

  private NameService getOriginalNameServiceMock() throws UnknownHostException {
    final NameService originalNameServiceMock = mock(NameService.class);
    final Inet4Address notGooglesIp = mock(Inet4Address.class);
    when(notGooglesIp.getAddress()).then(args -> GOOGLE_DNS_IP);
    when(originalNameServiceMock.lookupAllHostAddr(GOOGLE_DOMAIN)).then(args -> new InetAddress[] {notGooglesIp});
    return originalNameServiceMock;
  }

}
