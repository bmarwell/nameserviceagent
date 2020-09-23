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

package io.github.bmhm.nameserviceagent.wire;

import org.xbill.DNS.Flags;
import org.xbill.DNS.Message;
import org.xbill.DNS.Opcode;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;

public class DnsMessage extends Message {

  public static DnsMessage newQuery(final Record record) {
    final DnsMessage dnsMessage = new DnsMessage();
    dnsMessage.getHeader().setOpcode(Opcode.QUERY);
    dnsMessage.getHeader().setFlag(Flags.RD);
    dnsMessage.addRecord(record, Section.QUESTION);

    return dnsMessage;
  }
}
