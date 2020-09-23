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
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PingCommandTester implements ReachableTester {

  private static final String OS_NAME = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);

  @Override
  public boolean isReachable(final InetAddress addressToTest, final int timeoutMs) throws IOException {
    try {
      final ProcessBuilder pingCommandBuilder =
          new ProcessBuilder().command("ping", this.getNumberFlag(), "1", this.toStringIp(addressToTest));
      final Process pingCommand = pingCommandBuilder.start();
      final boolean exited = pingCommand.waitFor(timeoutMs, TimeUnit.MILLISECONDS);

      if (!exited) {
        pingCommand.destroy();
        return false;
      }

      final int exitValue = pingCommand.exitValue();

      return exitValue == 0;
    } catch (final InterruptedException javaLangInterruptedException) {
      return false;
    }

  }

  private String getNumberFlag() {
    if (OS_NAME.startsWith("win")) {
      return "-n";
    }

    return "-c";
  }

  private String toStringIp(final InetAddress addressToTest) {
    return addressToTest.getHostAddress();
  }
}
