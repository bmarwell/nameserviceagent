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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.bmhm.nameserviceagent.api.NameService;

import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

class DefaultSequentialRetryingNameServiceTest {

  private static final byte[] GOOGLE_DNS_IP = {8, 8, 8, 8};
  public static final String GOOGLE_COM_DOMAIN = "google.com";

  @Test
  void testPassThroughGetHostByAddr() throws UnknownHostException {
    // given
    final NameService original = mock(NameService.class);
    final DefaultSequentialRetryingNameService defaultSequentialRetryingNameService = new DefaultSequentialRetryingNameService(original);

    // when
    defaultSequentialRetryingNameService.getHostByAddr(GOOGLE_DNS_IP);

    // then
    verify(original).getHostByAddr(GOOGLE_DNS_IP);
  }

  @Test
  void testTryThreeTimes() throws IOException {
    // given
    final NameService original = mock(NameService.class);
    final DefaultSequentialRetryingNameService defaultSequentialRetryingNameService = new DefaultSequentialRetryingNameService(original);
    final InetAddress fakeInetAddress = this.proxyGoogleIpv4();

    // when
    when(original.lookupAllHostAddr(GOOGLE_COM_DOMAIN)).then(args -> new InetAddress[] {fakeInetAddress});
    final InetAddress[] inetAddresses = defaultSequentialRetryingNameService.lookupAllHostAddr(GOOGLE_COM_DOMAIN);

    // then
    assertTrue(inetAddresses.length == 1);
    verify(original).lookupAllHostAddr(GOOGLE_COM_DOMAIN);
    verify(fakeInetAddress, times(3)).isReachable(anyInt());

  }

  private InetAddress proxyGoogleIpv4() {
    final Inet4Address mock = mock(Inet4Address.class);
    try {
      when(mock.isReachable(any(int.class))).thenAnswer(new AnswerOnThree());
    } catch (final IOException javaIoIOException) {
      throw new IllegalStateException(javaIoIOException);
    }

    return mock;
  }

  private class AnswerOnThree implements Answer<Boolean> {
    int tries = 0;

    @Override
    public Boolean answer(final InvocationOnMock invocation) {
      this.tries++;
      return 3 == this.tries;
    }
  }
}
