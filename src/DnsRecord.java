import java.util.List;

public class DnsRecord {
    // Datastructure to store and print received records

    // Header
    short id; // 16 bits
    boolean QR;
    OpCode opCode; // 4 bits
    RCode rCode; // 4 bits

    char QDCOUNT = 1;
    char ANCOUNT;
    char NSCOUNT;
    char ARCOUNT;

    // Question
    List<DnsLabel> QName; // no specification on max length of QName
    DnsQueryType qType;
    short QCLASS = 0x0001;

    // Answer
    String NAME;
    DnsQueryType TYPE;
    short CLASS;
    long TTL; // should be 32 bit unsigned
    int RDLENGTH; // should be unsigned 16 bit
    RData rData;

}
