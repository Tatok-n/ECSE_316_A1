public class Ipv6Rdata implements RData{
    private byte[] ip;
    @Override
    public String getRdata() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i += 2) {
            int segment = ((ip[i] & 0xFF) << 8) | (ip[i + 1] & 0xFF);
            sb.append(Integer.toHexString(segment));
            if (i < 14) {
                sb.append(":");
            }
        }
        return sb.toString();
    }

    public Ipv6Rdata(byte[] ip) {
        this.ip = ip;
    }
}
