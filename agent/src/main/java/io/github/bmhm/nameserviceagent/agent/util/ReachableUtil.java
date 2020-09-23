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

package io.github.bmhm.nameserviceagent.agent.util;

import java.io.IOException;
import java.net.InetAddress;

public final class ReachableUtil {

  private static final Integer TIMEOUT_MS = Integer.getInteger("nameserviceagent.reachable.timeoutMs", 100);

  private static final ReachableTester[] REACHABLE_TESTS = new ReachableTester[] {
      new IcmpTester(),
      new PingCommandTester(),
      new SocketOpenTester()
  };

  private ReachableUtil() {
    // util class
  }

  public static boolean isReachable(final InetAddress address) {
    for (final ReachableTester test : REACHABLE_TESTS) {
      final boolean isReachable = doTestInternal(address, test);

      if (isReachable) {
        return true;
      }
    }

    return false;
  }

  private static boolean doTestInternal(final InetAddress address, final ReachableTester test) {
    try {
      return test.isReachable(address, TIMEOUT_MS);
    } catch (final IOException ioException) {
      return false;
    }
  }


}
