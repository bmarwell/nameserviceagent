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

import java.net.InetAddress;
import java.net.UnknownHostException;

public class AlwaysLocalhostLoopbackNameService extends AbstractProxyNameService {

  public AlwaysLocalhostLoopbackNameService(final NameService originalNameService) {
    super(originalNameService);
  }

  @Override
  public InetAddress[] lookupAllHostAddr(final String host) throws UnknownHostException {
    return new InetAddress[] {InetAddress.getLoopbackAddress()};
  }

  @Override
  public String getHostByAddr(final byte[] addr) throws UnknownHostException {
    return "localhost";
  }
}
