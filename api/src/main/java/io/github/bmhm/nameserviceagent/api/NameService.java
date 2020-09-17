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

package io.github.bmhm.nameserviceagent.api;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Basic interface for returning addresses for a host lookup or a hostname (reverse lookup) for an address.
 *
 * <p><strong>Implementation notice:</strong> Do not use this directly.
 * You must use the {@link AbstractProxyNameService} instead. The agent depends on this.</p>
 */
public interface NameService {

  /**
   * Lookup a host mapping by name. Retrieve the IP addresses associated with a host
   *
   * @param host the specified hostname to resolve.
   * @return array of IP addresses for the requested host
   * @throws UnknownHostException if no IP address for the {@code host} could be found
   */
  InetAddress[] lookupAllHostAddr(final String host) throws UnknownHostException;

  /**
   * Lookup the host corresponding to the IP address provided.
   *
   * <p>This method is called by {@link java.net.InetAddress}{@code #getHostFromNameService(InetAddress, boolean)}.</p>
   *
   * @param addr byte array representing an IP address
   * @return {@code String} representing the host name mapping
   * @throws UnknownHostException if no host found for the specified IP address
   */
  String getHostByAddr(final byte[] addr) throws UnknownHostException;

}
