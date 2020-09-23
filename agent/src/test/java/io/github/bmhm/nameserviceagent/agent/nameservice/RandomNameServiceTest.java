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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.bmhm.nameserviceagent.api.NameService;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.stream.IntStream;

public class RandomNameServiceTest {

  private final NameService originalNameService = mock(NameService.class);

  private final RandomNameService randomNameService = new RandomNameService(this.originalNameService);

  @Test
  void testIsReachable() throws UnknownHostException {
    // given fake resolved ip
    final InetAddress fakeInetAddress = ProxyIpv4Helper.proxyGoogleIpv4();

    when(this.originalNameService.lookupAllHostAddr(any())).thenAnswer(
        args -> new InetAddress[] {fakeInetAddress}
    );

    // when
    final InetAddress[] inetAddresses = this.randomNameService.lookupAllHostAddr("google.com");

    assertEquals(fakeInetAddress, inetAddresses[0]);
  }

  @Test
  void testIsReachable_multiple() throws UnknownHostException {
    // given fake resolved ip
    final InetAddress[] fakeInetAddresses = IntStream.range(0, 20)
        .mapToObj(anInt -> ProxyIpv4Helper.proxyGoogleIpv4())
        .toArray(InetAddress[]::new);

    when(this.originalNameService.lookupAllHostAddr(any())).thenAnswer(
        args -> fakeInetAddresses
    );

    // when
    final InetAddress[] inetAddresses = this.randomNameService.lookupAllHostAddr("google.com");

    assertEquals(1, inetAddresses.length);
    assertThat(fakeInetAddresses, Matchers.hasItemInArray(inetAddresses[0]));
  }
}
