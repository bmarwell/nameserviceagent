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

import io.github.bmhm.nameserviceagent.agent.util.ReachableUtil;
import io.github.bmhm.nameserviceagent.api.AbstractProxyNameService;
import io.github.bmhm.nameserviceagent.api.NameService;
import io.github.bmhm.nameserviceagent.wire.DnsMessage;

import org.xbill.DNS.AAAARecord;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.DohResolver;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.Section;
import org.xbill.DNS.Type;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DnsOverHttpsNameService extends AbstractProxyNameService {

  private static final Logger LOG = Logger.getLogger(DnsOverHttpsNameService.class.getCanonicalName());

  private static final boolean PREFER_IPV4 = Boolean.getBoolean("java.net.preferIPv4Stack");

  /**
   * The original name service will get injected.
   *
   * @param originalNameService the original name service before proxying.
   */
  public DnsOverHttpsNameService(final NameService originalNameService) {
    super(originalNameService);
  }

  @Override
  public InetAddress[] lookupAllHostAddr(final String host) throws UnknownHostException {
    final Set<InetAddress> addresses = new LinkedHashSet<>();

    if (PREFER_IPV4) {
      addresses.addAll(this.lookupAllHostV4Addr(host));
      addresses.addAll(this.lookupAllHostV6Addr(host));
    } else {
      addresses.addAll(this.lookupAllHostV6Addr(host));
      addresses.addAll(this.lookupAllHostV4Addr(host));
    }

    return addresses.stream()
        .filter(ReachableUtil::isReachable)
        .distinct()
        .toArray(InetAddress[]::new);
  }

  private Collection<InetAddress> lookupAllHostV4Addr(final String host) {
    return this.doLookupAllHostAddr(host, Type.A, Inet4Address.class);
  }

  private Collection<InetAddress> lookupAllHostV6Addr(final String host) {
    return this.doLookupAllHostAddr(host, Type.AAAA, Inet6Address.class);
  }

  private <T extends InetAddress> Collection<InetAddress> doLookupAllHostAddr(
      final String host,
      final int type,
      final Class<T> targetType) {
    try {
      final Name hostName = Name.fromString(host + ".");
      final Record ipv4Record = Record.newRecord(hostName, type, DClass.IN);
      final DnsMessage dnsMessage = DnsMessage.newQuery(ipv4Record);

      final Message response = this.getResolver().send(dnsMessage);
      final List<Record> answerSection = response.getSection(Section.ANSWER);
      final Set<InetAddress> addresses = new LinkedHashSet<>();

      for (final Record answer : answerSection) {
        if (answer.getType() != type) {
          continue;
        }

        switch (targetType.getSimpleName()) {
          case "Inet4Address":
            final ARecord aRecord = (ARecord) answer;
            final InetAddress inet4Address = aRecord.getAddress();
            addresses.add(inet4Address);
            break;
          case "Inet6Address":
            final AAAARecord aaaaRecord = (AAAARecord) answer;
            final InetAddress inet6Address = aaaaRecord.getAddress();
            addresses.add(inet6Address);
            break;
          default:
            throw new IllegalArgumentException("Unknown target type: " + targetType);
        }
      }

      return addresses;
    } catch (final IOException javaIoIOException) {
      LOG.log(
          Level.WARNING,
          "Network unreachable for DNS server response: [" + host + "].",
          javaIoIOException);

      return Collections.emptySet();
    }
  }

  private Resolver getResolver() {
    final DohResolver dohResolver = new DohResolver("https://dns.google/dns-query");
    dohResolver.setTimeout(Duration.ofMillis(5000L));

    return dohResolver;
  }

  @Override
  public String getHostByAddr(final byte[] addr) throws UnknownHostException {
    throw new UnsupportedOperationException("not implemented");
  }

}
