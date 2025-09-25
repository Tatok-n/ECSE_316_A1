import java.net.InetAddress;

public class DnsClient {
    /*
     * main app, handles:
     * - parsing CLI arguments
     * - calls request builder to make a request
     * - uses dnssocket to send/receive
     * - calls response parser on response
     * - prints result
     */

    public static void main(String[] args) {
        if (args.length < 3) {
            throw new IllegalArgumentException("Usage: java DnsClient <dns-server-ip> <domain-name> <query-type>");
        }

        try {
            byte[] serverIp = InetAddress.getByName(args[0]).getAddress(); //Not sure if this is allowd ??
            String domainName = args[1];
            String queryTypeStr = args[2];

            DnsQueryType queryType = DnsQueryType.fromName(queryTypeStr);
            DnsRequestBuilder builder = new DnsRequestBuilder();
            byte[] requestPacket = builder.buildRequest(domainName, queryType);

            // TODO : Add extra arguments
            DnsSocket dnsSocket = new DnsSocket(serverIp, 53, 5, 3);

            byte[] responsePacket = dnsSocket.query(requestPacket);
            DnsResponseParser parser = new DnsResponseParser(responsePacket);
            DnsRecord record = parser.parse();

            // Print answers
            printAnswers(record);
            dnsSocket.close();

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(99);
        }
    }

    private static void printAnswers(DnsRecord record) {
        // TODO : IMPLEMENT FUNCTION, Consult Assignment doc and parser for specifics
    }

}
