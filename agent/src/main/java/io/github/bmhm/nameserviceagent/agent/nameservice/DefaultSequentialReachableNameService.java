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
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Similar to the original dns service, but takes all returned IPs into consideration and tries each three times
 * before giving up.
 *
 * <p>The original service will only use the first response from the name service without any checking.</p>
 */
public class DefaultSequentialReachableNameService extends AbstractProxyNameService {

  private static final Logger LOG = Logger.getLogger(DefaultSequentialReachableNameService.class.getCanonicalName());
  private final Integer timeoutMs;

  public DefaultSequentialReachableNameService(final NameService originalNameService) {
    super(originalNameService);
    this.timeoutMs =
        Integer.getInteger("nameserviceagent.DefaultSequentialReachableNameService.timeoutMs", 100);
  }

  @Override
  public InetAddress[] lookupAllHostAddr(final String host) throws UnknownHostException {
    final InetAddress[] inetAddresses = this.getOriginalNameService().lookupAllHostAddr(host);
    // using a linked hashset, because we want to retain the original order.
    final Set<InetAddress> reachableAddresses = new LinkedHashSet<>(inetAddresses.length);

    for (final InetAddress inetAddress : inetAddresses) {
      if (!this.isReachable(inetAddress)) {
        continue;
      }

      reachableAddresses.add(inetAddress);
    }

    if (reachableAddresses.isEmpty()) {
      throw new UnknownHostException(
          "Unable to resolve host [" + host + "]: "
              + "none of the resolved IP addresses is reachable: " + Arrays.toString(inetAddresses)
      );
    }

    return reachableAddresses.toArray(new InetAddress[0]);
  }

  private boolean isReachable(final InetAddress inetAddress) {
    try {
      return inetAddress.isReachable(this.timeoutMs);
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
