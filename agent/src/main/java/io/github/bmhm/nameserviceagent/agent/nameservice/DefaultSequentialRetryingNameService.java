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

import io.github.bmhm.nameserviceagent.api.AbstractProxyNameService;
import io.github.bmhm.nameserviceagent.api.NameService;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Similar to the original dns service, but takes all returned IPs into consideration and tries each three times
 * before giving up.
 *
 * <p>The original service will only use the first response from the name service without any checking.</p>
 */
public class DefaultSequentialRetryingNameService extends AbstractProxyNameService {

  private static final Logger LOG = Logger.getLogger(DefaultSequentialRetryingNameService.class.getCanonicalName());
  private final Integer timeoutMs;

  public DefaultSequentialRetryingNameService(final NameService originalNameService) {
    super(originalNameService);
    this.timeoutMs =
        Integer.getInteger("io.github.bmhm.nameserviceagent.agent.nameservice.DefaultSequentialRetryingNameService.timeoutMs", 100);
  }

  @Override
  public InetAddress[] lookupAllHostAddr(final String host) throws UnknownHostException {
    final InetAddress[] inetAddresses = this.getOriginalNameService().lookupAllHostAddr(host);

    final InetAddress[] aliveAddresses = Arrays.stream(inetAddresses)
        .filter(this::isReachableMultiple)
        .toArray(InetAddress[]::new);

    if (aliveAddresses.length == 0) {
      throw new UnknownHostException("Unable to resolve host " + host);
    }

    return aliveAddresses;
  }

  private boolean isReachableMultiple(final InetAddress inetAddress) {
    boolean reachable = false;

    for (int tries = 3; tries >= 1; tries--) {
      if (reachable = this.isReachable(inetAddress)) {
        break;
      }
    }

    if (!reachable) {
      LOG.log(Level.FINE, "DNS returned unreachable server: [" + inetAddress.toString() + "].");
    }

    return reachable;

  }

  private boolean isReachable(final InetAddress inetAddress) {
    try {
      return inetAddress.isReachable(100);
    } catch (final IOException javaIoIOException) {
      LOG.log(Level.FINE, "Network unreachable for DNS server response: [" + inetAddress.toString() + "].");
    }

    return false;
  }

  @Override
  public String getHostByAddr(final byte[] addr) throws UnknownHostException {
    return this.getOriginalNameService().getHostByAddr(addr);
  }

}
