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
        if (args.length < 2 || args.length > 10) {
            printUsageError();
        }

        try {
            // Defaults
            int timeout = 5;
            int maxRetries = 3;
            int port = 53;
            DnsQueryType queryType = DnsQueryType.A;

            // Parse optional flags
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

            // Validate trailing args
            if (args.length - i != 2) {
                printUsageError();
            }

            String serverArg = args[args.length - 2];
            String domainName = args[args.length - 1];

            if (!serverArg.startsWith("@")) {
                System.err.println("ERROR\tDNS server IP address must start with @ : " + serverArg);
                System.exit(1);
            }

            byte[] serverIp = parseIp(serverArg.substring(1));

            // Print request summary
            System.out.println("DnsClient sending request for " + domainName);
            System.out.println("Server : " + InetAddress.getByAddress(serverIp));
            System.out.println("Request type: " + queryType);

            // Build request
            DnsRequestBuilder builder = new DnsRequestBuilder();
            byte[] requestPacket = builder.buildRequest(domainName, queryType);

            // Send request & get response
            DnsSocket dnsSocket = new DnsSocket(serverIp, port, timeout, maxRetries);

            byte[] responsePacket = dnsSocket.query(requestPacket);

            // Parse response
            DnsResponseParser parser = new DnsResponseParser(responsePacket);
            DnsRecord record = parser.parse();

            // Print answers
            printAnswers(record);

            dnsSocket.close();

        } catch (Exception e) {
            System.err.println("ERROR\t" + e.getMessage());
            e.printStackTrace();
            System.exit(99);
        }
    }

    private static void printUsageError() {
        System.err.println("ERROR\tIncorrect input syntax");
        System.err.println("Usage: java DnsClient [-t timeout] [-r max-retries] [-p port] [-mx|-ns] @server name");
        System.exit(1);
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
                throw new IllegalArgumentException(
                        "Invalid IPv4 address, characters are not allowed in an IPV4 address: " + ipStr);
            }
        }
        return addr;
    }

    private static void printAnswers(DnsRecord record) {
        int ansNb = record.getNumberOfAnswers();
        int addAndNb = record.getNumberOfAdditionalRecords();
        if (ansNb == 0 && addAndNb == 0) {
            System.out.println("NOTFOUND");
            return;
        }
        if (ansNb > 0) {
            System.out.println("***Answer Section (" + ansNb + " records)***");
            for (DnsRR answer : record.getAnswers()) {
                System.out.println(answer.toString(record.isAuth()));
            }
        }
        if (addAndNb > 0) {
            System.out.println("***Additional Section (" + addAndNb + " records)***");
            for (DnsRR additonalRecord : record.getAdditionalRecords()) {
                System.out.println(additonalRecord.toString(record.isAuth()));
            }
        }
    }

}
