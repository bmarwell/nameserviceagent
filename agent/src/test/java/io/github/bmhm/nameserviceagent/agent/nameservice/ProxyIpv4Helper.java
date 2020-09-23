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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;

public final class ProxyIpv4Helper {

  public static final byte[] GOOGLE_IP = {(byte) 216, 58, (byte) 212, (byte) 174};

  private ProxyIpv4Helper() {
    // util class
  }

  public static InetAddress proxyGoogleIpv4() {
    try {
      final InetAddress mock = mock(Inet4Address.class);
      when(mock.getAddress()).then(args -> GOOGLE_IP);
      when(mock.isReachable(any(int.class))).then(args -> true);

      return mock;
    } catch (final IOException javaIoIOException) {
      throw new IllegalStateException(javaIoIOException);
    }
  }

}
