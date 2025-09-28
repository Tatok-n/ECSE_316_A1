public class Ipv4Rdata implements RData {

    private final String ip;

    @Override
    public String getRdata() {
        return ip;
    }

    public Ipv4Rdata(int ip) {
        String computed = intToIpv4(ip);
        validateIpv4(computed);
        this.ip = computed;
    }

    private String intToIpv4(int ip) {
        int b1 = (ip >> 24) & 0xFF;
        int b2 = (ip >> 16) & 0xFF;
        int b3 = (ip >> 8) & 0xFF;
        int b4 = ip & 0xFF;
        return b1 + "." + b2 + "." + b3 + "." + b4;
    }

    private void validateIpv4(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid IPv4 format: " + ip);
        }
        for (String part : parts) {
            try {
                int value = Integer.parseInt(part);
                if (value < 0 || value > 255) {
                    throw new IllegalArgumentException("IPv4 octet out of range: " + part);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid IPv4 octet: " + part);
            }
        }
    }
}