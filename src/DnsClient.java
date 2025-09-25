import java.net.InetAddress;
import java.util.Arrays;

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
        if (args.length < 2) {
            throw new IllegalArgumentException("Usage: java DnsClient <dns-server-ip> <domain-name> <query-type>");
        }

        try {
            int timeout = 5;
            int maxRetries = 3;
            int port = 53;
            DnsQueryType queryType = DnsQueryType.A;
            byte[] serverIp;
            String domainName;
            int i = 0;

            while (i < args.length - 2) {
                String arg = args[i];
                switch (arg) {
                    case "-t":
                        timeout = Integer.parseInt(args[++i]);
                        break;
                    case "-r":
                        maxRetries = Integer.parseInt(args[++i]);
                        break;
                    case "-p":
                        port = Integer.parseInt(args[++i]);
                        break;
                    case "-mx":
                        queryType = DnsQueryType.MX;
                        break;
                    case "-ns":
                        queryType = DnsQueryType.NS;
                        break;
                    default:
                        break;
                }
                i++;

            }

            if (!(args[i].startsWith("@"))) {
                throw new IllegalArgumentException("DNS server IP address must start with @ : " + args[i]);
            }

            serverIp = parseIp(args[i].substring(1));
            domainName = args[i + 1];

            System.out.println("Server : " + InetAddress.getByAddress(serverIp));
            System.out.println("DnsClient sending request for : " + domainName);
            System.out.println("Request type: " + queryType);


            DnsRequestBuilder builder = new DnsRequestBuilder();
            byte[] requestPacket = builder.buildRequest(domainName, queryType);

            DnsSocket dnsSocket = new DnsSocket(serverIp, port, timeout, maxRetries);

            byte[] responsePacket = dnsSocket.query(requestPacket);
            DnsResponseParser parser = new DnsResponseParser(responsePacket);
            DnsRecord record = parser.parse();

            // Print answers
            printAnswers(record);
            dnsSocket.close();

        } catch (Exception e) {
            System.err.println("ERROR " + e.getMessage());
            System.exit(99);
        }
    }

    private static byte[] parseIp(String ipStr) {
        String[] parts = ipStr.split("\\.");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid IPv4 address: " + ipStr);
        }
        byte[] addr = new byte[4];
        for (int i = 0; i < 4; i++) {
            try {
                int val = Integer.parseInt(parts[i]);
                if (val < 0 || val > 255) {
                    throw new IllegalArgumentException("Invalid octet in IP: " + parts[i]);
                }
                addr[i] = (byte) val;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid IPv4 address, strings are not allowed in an IPV4 address: " + ipStr);
            }
        }
        return addr;
    }

    private static void printAnswers(DnsRecord record) {
        System.out.println("***Answer Section("+ record.getNumberOfAnswers()+" records)***");
        for (DnsAnswer answer : record.getAnswers()) {
            System.out.println(answer.toString(record.isAuth()));
        }

        if (record.getNumberOfAdditionalRecords() != 0) {
            System.out.println("***Additional Section("+ record.getNumberOfAdditionalRecords()+" records)***");
        } else {
            System.out.println("NOTFOUND");
        }
    }

}
