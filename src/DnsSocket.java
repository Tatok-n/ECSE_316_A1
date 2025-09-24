import java.net.*;
import java.util.Arrays;

public class DnsSocket {
    // Handles UDP socket setup and communication with retries/timeout
    private InetAddress serverAddress;
    private int port;
    private int timeout; // milliseconds
    private int maxRetries;

    private DatagramSocket socket;

    public DnsSocket(byte[] serverIP, int port, int timeout, int maxRetries) throws Exception {
        this.serverAddress = InetAddress.getByAddress(serverIP);
        this.port = port;
        this.timeout = timeout * 1000; // convert seconds -> ms
        this.maxRetries = maxRetries;
        this.socket = new DatagramSocket();
        this.socket.setSoTimeout(this.timeout);
    }

    /** Send a DNS query packet */
    public void send(byte[] queryPacket) throws Exception {
        DatagramPacket request = new DatagramPacket(queryPacket, queryPacket.length, serverAddress, port);
        socket.send(request);
    }

    /** Receive a DNS response packet */
    public byte[] receive() throws Exception {
        DatagramPacket response = new DatagramPacket(new byte[512], 512);
        socket.receive(response);
        return Arrays.copyOf(response.getData(), response.getLength());
    }

    /**
     * Send and receive with timeout + retry logic
     */
    public byte[] queryWithRetry(byte[] queryPacket) throws Exception {
        int attempts = 0;
        while (attempts < maxRetries) {
            try {
                long start = System.currentTimeMillis();
                send(queryPacket); // send
                byte[] response = receive(); // wait for response

                long elapsed = System.currentTimeMillis() - start;
                System.out.println("Response received after " + (elapsed / 1000.0) +
                        " seconds (" + attempts + " retries)");
                return response;

            } catch (SocketTimeoutException e) {
                attempts++;
                if (attempts >= maxRetries) {
                    throw new Exception("ERROR\tMaximum number of retries " + maxRetries + " exceeded");
                }
                System.out.println("Timeout... retrying (" + attempts + ")");
            }
        }
        return null; // shouldn’t happen
    }

    /** Close the socket */
    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
