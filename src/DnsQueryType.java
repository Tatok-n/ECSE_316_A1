public enum DnsQueryType {
    A(0x0001), // IPv4 address
    NS(0x0002), // Name server
    MX(0x000f); // Mail server

    public final int value;

    DnsQueryType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}