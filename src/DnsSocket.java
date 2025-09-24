import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;

public class DnsSocket {
    // Class to handle opening/closing UDP socket
    // Should handle timeout, retries
    private InetAddress serverAddress; // DNS server IP
    private int port; // DNS server port (default 53)
    private int timeout; // timeout in milliseconds
    private int maxRetries; // maximum retries

    private DatagramSocket socket;

    public void DNSsocket(String serverIP, int port, int timeout, int maxRetries) throws Exception {
        // creates a new socket
        this.serverAddress = InetAddress.getByName(serverIP);
        this.port = port;
        this.timeout = timeout * 1000; // convert seconds to ms
        this.maxRetries = maxRetries;
        this.socket = new DatagramSocket();
        this.socket.setSoTimeout(this.timeout);
    }

    public void send(byte[] queryPacket) throws Exception {
        // send datagram
        DatagramPacket request = new DatagramPacket(queryPacket, queryPacket.length, serverAddress, port);
        socket.send(request);
    }

    public byte[] receive() throws Exception {
        // receive response
        DatagramPacket response = new DatagramPacket(new byte[512], 512);
        socket.receive(response);
        return Arrays.copyOf(response.getData(), response.getLength());
    }

    public void close() {
        // closes socket
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

}
