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

            try {
                while (i < args.length - 2) {
                    String arg = args[i];
                    if (arg.equals("-t")) {
                        timeout = Integer.parseInt(args[++i]);
                        i++;
                    } else if (arg.equals("-r")) {
                        maxRetries = Integer.parseInt(args[++i]);
                        i++;
                    } else if (arg.equals("-p")) {
                        port = Integer.parseInt(args[++i]);
                        i++;
                    } else if (arg.equals("-mx") && queryType == DnsQueryType.A) {
                        queryType = DnsQueryType.MX;
                        i++;
                    } else if (arg.equals("-ns") && queryType == DnsQueryType.A) {
                        queryType = DnsQueryType.NS;
                        i++;
                    } else {
                        printUsageError();
                    }
                }
            } catch (NumberFormatException e) {
                printNumberFormatError(args[i - 1], args[i]);
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

            // Open socket
            DnsSocket dnsSocket = new DnsSocket(serverIp, port, timeout, maxRetries);

            // Build request
            DnsRequestBuilder builder = new DnsRequestBuilder();
            byte[] requestPacket = builder.buildRequest(domainName, queryType);

            // only send if valid request
            if (builder.validateRequest(requestPacket, domainName, queryType)) {
                // Send request & get response
                byte[] responsePacket = dnsSocket.query(requestPacket);

                // Parse response
                DnsResponseParser parser = new DnsResponseParser(responsePacket);
                DnsResponse record = parser.parse();

                // Print answers
                printAnswers(record);
            } else {
                System.out.println("Invalid Request Format");
            }

            dnsSocket.close();

        } catch (DnsNotFoundException e) {
            System.out.println("NOTFOUND");
            System.exit(0); // successfully not found
        } catch (Exception e) {
            System.err.println("ERROR\t" + e.getMessage());
            System.exit(99);
        }
    }

    private static void printUsageError() {
        System.err.println("ERROR\tIncorrect input syntax");
        System.err.println("Usage: java DnsClient [-t timeout] [-r max-retries] [-p port] [-mx|-ns] @server name");
        System.exit(1);
    }

    private static void printNumberFormatError(String arg, String value) {
        System.err.println("ERROR\tIncorrect input syntax");
        System.err.println(
                "ERROR\tIncorrect input format : " + arg + " was expected to be an integer but was : " + value + ".");
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

    private static void printAnswers(DnsResponse record) {
        int ansNb = record.getNumberOfAnswers();
        int addAndNb = record.getNumberOfAdditionalRecords();

        if (ansNb == 0 && addAndNb == 0) {
            System.out.println("NOT FOUND");
            return;
        }
        if (ansNb > 0) {
            System.out.println("***Answer Section (" + ansNb + " records)***");
            for (DnsResourceRecord answer : record.getAnswers()) {
                System.out.println(answer.toString(record.isAuth()));
            }
        }
        if (addAndNb > 0) {
            System.out.println("***Additional Section (" + addAndNb + " records)***");
            for (DnsResourceRecord additonalRecord : record.getAdditionalRecords()) {
                System.out.println(additonalRecord.toString(record.isAuth()));
            }
        }
    }

}
