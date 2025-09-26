public class Ipv4Rdata implements RData{

    int ip;
    @Override
    public String getRdata() {
        int b1 = (ip >> 24) & 0xFF;
        int b2 = (ip >> 16) & 0xFF;
        int b3 = (ip >> 8) & 0xFF;
        int b4 = ip & 0xFF;
        return String.join(".", String.valueOf(b1), String.valueOf(b2), String.valueOf(b3), String.valueOf(b4));
    }

    public Ipv4Rdata(int ip) {
        this.ip = ip;
    }

}
