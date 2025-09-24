import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

public class DnsRequestBuilder {
    // Builds the query packets (raw bytes) to be sent to the DNS server
    // Should handle the different query types and our desired settings

    private final Random random = new Random(); // for our random ids

    public byte[] buildRequest(String domainName, DnsQueryType reqType) {
        // build a request for the domainName and the type
        short id = (short) random.nextInt(0xFFFF);
        ByteBuffer buffer = ByteBuffer.allocate(512);// overallocate and truncate later

        // HEADER
        // ID
        buffer.putShort(id);
        // QR=0 OPcode=0000 AA=0 TC=0 RD=1 RA=0 Z=000 RCODE=0000
        buffer.putShort((short) 0x0100);
        // QDCOUNT = 1
        buffer.putShort((short) 0x0001);
        // ANCOUNT = 0
        buffer.putShort((short) 0x0000);
        // NSCOUNT = 0
        buffer.putShort((short) 0x0000);
        // ARCOUNT = 0
        buffer.putShort((short) 0x0000);

        // QUESTION
        // QNAME
        buffer.put(encodeName(domainName));
        // QTYPE
        buffer.putShort((short) reqType.value);
        // QCLASS
        buffer.putShort((short) 0x0001);

        return Arrays.copyOf(buffer.array(), buffer.position());
    }

    public byte[] encodeName(String domainName) {
        String[] labels = domainName.split("\\.");
        // max domain length 256 bytes
        ByteBuffer buffer = ByteBuffer.allocate(256);

        for (String label : labels) {
            // label size
            buffer.put((byte) label.length());
            // each char
            for (char c : label.toCharArray()) {
                buffer.put((byte) c);
            }
        }
        // QNAME end
        buffer.put((byte) 0);
        return Arrays.copyOf(buffer.array(), buffer.position());
    }

}
