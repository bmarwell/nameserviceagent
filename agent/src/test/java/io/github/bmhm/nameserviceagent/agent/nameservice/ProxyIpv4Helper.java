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

  private ProxyIpv4Helper() {
    // util class
  }

  public static InetAddress proxyGoogleIpv4() {
    final Inet4Address mock = mock(Inet4Address.class);
    try {
      when(mock.isReachable(any(int.class))).then(args -> true);
    } catch (final IOException javaIoIOException) {
      throw new IllegalStateException(javaIoIOException);
    }

    return mock;
  }

}
