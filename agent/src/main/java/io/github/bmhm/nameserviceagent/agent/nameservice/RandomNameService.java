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
import java.security.SecureRandom;
import java.util.Random;

/**
 * Randomly selects one of the received IPs without checking availability.
 */
public class RandomNameService extends AbstractProxyNameService {

  private static final Random RANDOM = new SecureRandom();

  /**
   * The original name service will get injected.
   *
   * @param originalNameService the original name service before proxying.
   */
  public RandomNameService(final NameService originalNameService) {
    super(originalNameService);
  }

  @Override
  public InetAddress[] lookupAllHostAddr(final String host) throws UnknownHostException {
    final InetAddress[] inetAddresses = this.getOriginalNameService().lookupAllHostAddr(host);
    final int index = RANDOM.nextInt(inetAddresses.length);

    return new InetAddress[] {inetAddresses[index]};
  }

  @Override
  public String getHostByAddr(final byte[] addr) throws UnknownHostException {
    return this.getOriginalNameService().getHostByAddr(addr);
  }
}
