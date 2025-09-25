import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DnsResponseParser {
    // parses the packet received from dns server and store it in a DnsRecord
    // should handle if the packets are compressed
    private final byte[] packet;
    private int position;

    public DnsResponseParser(byte[] packet) {
        this.packet = packet;
        this.position = 0;
    }

    public List<DnsLabel> readName() {
        int pos = this.position;
        List<DnsLabel> labels = new ArrayList<>();
        boolean jumped = false;
        int jumpPos = -1;
        while(true) {
            int len = packet[pos] & 0xFF;
            if (len == 0) {
                pos++;
                break;
            }

            if ((len & 0xC0) == 0xC0) {
                int pointer = ((len & 0x3F) << 8) | (packet[pos + 1] & 0xFF);
                if (!jumped) {
                    jumpPos = pos + 2;
                }
                pos = pointer;
                jumped = true;
                continue;
            }

            pos++;
            byte labelLength = (byte) len;

            String labelName = new String(packet, pos, labelLength);
            labels.add(new DnsLabel(labelLength, labelName));
            pos += labelLength;
        }

        position = jumped ? jumpPos : pos;
        return labels;
    }

    // Main parse method: parses entire response into DnsRecord objects
    public DnsRecord parse() {
        DnsRecord record = new DnsRecord();

        position = 0;

        // parse Header
        int id = ((packet[position] & 0xFF) << 8) | (packet[position + 1] & 0xFF);
        position += 2;

        int flags = ((packet[position] & 0xFF) << 8) | (packet[position + 1] & 0xFF);
        boolean qr = ((flags >> 15) & 0x1) == 1;
        int codeInt =  ((flags >> 11) & 0xF);
        OpCode opCode = new OpCode((codeInt & 0x8) != 0, (codeInt & 0x4) != 0, (codeInt & 0x2) != 0, (codeInt & 0x1) != 0);
        RCode rCode = RCode.fromValue(flags & 0xF);
        position += 2;

        int qdCount = readUnsignedInt();
        int anCount = readUnsignedInt();
        int nsCount = readUnsignedInt();
        int arCount = readUnsignedInt();

        record.setHeader(id, qr, opCode, rCode,qdCount,anCount,nsCount, arCount);

        //Parse Question(s) should be 1
        parseQuestions(qdCount, record);

        //Parse Answers
        parseAnswers(anCount, record);
        return record;
    }

    private int readUnsignedInt() {
        int initialPos = position;
        position += 2;
        return ((packet[initialPos] & 0xFF) << 8) | (packet[initialPos + 1] & 0xFF);
    }

    private void parseAnswers(int anCount, DnsRecord record) {
        for (int i = 0; i < anCount; i++) {
            List<DnsLabel> labels = readName();
            int queryTypeInt = readUnsignedInt();
            int queryClassInt = readUnsignedInt();
            if (queryClassInt != 1) {
                throw new IllegalArgumentException("queryClassInt must be 1, it was : " + queryClassInt);
            }
            int higherTTLInt = readUnsignedInt();
            int lowerTTLInt = readUnsignedInt();
            long ttl = ((long) higherTTLInt << 16) | lowerTTLInt;
            int rdLen = readUnsignedInt();
            DnsQueryType queryType = DnsQueryType.fromValue(queryTypeInt);
            RData rData;
            if (queryType == DnsQueryType.A) {
                int higherIp = readUnsignedInt();
                int lowerIp = readUnsignedInt();
                int ip = (higherIp << 16) | lowerIp;
                rData = new IpRdata(ip);
            } else if (queryType == DnsQueryType.NS || queryType == DnsQueryType.CName) {
                rData = new DomainRdata(readName());
            } else {
                int preference = readUnsignedInt();
                labels = readName();
                rData = new MXRdata(preference,labels);
            }
            record.addAnswer(labels,queryType,queryClassInt, ttl, rdLen,rData );
        }
    }

    private void parseQuestions(int qdCount, DnsRecord record) {
        for (int i = 0; i < qdCount; i++) {
            List<DnsLabel> labels = readName();
            int queryTypeInt = readUnsignedInt();
            int queryClassInt = readUnsignedInt();
            if (queryClassInt != 1) {
                throw new IllegalArgumentException("queryClass must be 1, it was : " + queryClassInt);
            }
            DnsQueryType queryType = DnsQueryType.fromValue(queryTypeInt);
            record.addQuestion(labels,queryType,0x0001);
        }

    }
}
