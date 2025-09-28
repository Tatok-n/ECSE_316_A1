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

    public boolean validateRequest(byte[] request, String domainName, DnsQueryType reqType) {
        // min length: 12 bytes for header + domain length (assuming ascii
        // chars) + QTYPE + QCLASS
        if (request.length < 12 + domainName.length() + 2 + 2)
            return false;

        ByteBuffer buffer = ByteBuffer.wrap(request);

        // --- HEADER ---
        short id = buffer.getShort(); // ID, any value is fine
        short flags = buffer.getShort();
        if ((flags & 0x0100) == 0)
            return false; // RD bit should always be 1
        short qdCount = buffer.getShort();
        if (qdCount != 1)
            return false;
        short anCount = buffer.getShort();
        if (anCount != 0)
            return false;
        short nsCount = buffer.getShort();
        if (nsCount != 0)
            return false;
        short arCount = buffer.getShort();
        if (arCount != 0)
            return false;

        // --- QUESTION SECTION ---

        // Validate QNAME byte-by-byte
        int startPos = buffer.position();
        int maxPos = request.length - 4; // leave 4 bytes for QTYPE + QCLASS
        String[] labels = domainName.split("\\.");
        for (String label : labels) {
            if (buffer.position() >= maxPos)
                return false; // overflow
            byte len = buffer.get();
            if (len != label.length())
                return false;
            for (char c : label.toCharArray()) {
                if (buffer.get() != (byte) c)
                    return false;
            }
        }
        byte end = buffer.get();
        if (end != 0)
            return false; // QNAME must end with 0

        // Validate QTYPE
        short qType = buffer.getShort();
        if (qType != reqType.value)
            return false;

        // Validate QCLASS
        short qClass = buffer.getShort();
        if (qClass != 0x0001) // always use 1
            return false;

        return true; // all checks passed
    }

}
