public enum DnsQueryType {
    A(0x0001), // IPv4 address
    AAAA(0x001C), // IPv6 address
    NS(0x0002), // Name server
    CName(0x0005), // Name server
    MX(0x000f); // Mail server

    public final int value;

    DnsQueryType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static DnsQueryType fromValue(int type) {
        for (DnsQueryType qType : DnsQueryType.values()) {
            if (qType.value == type) {
                return qType;
            }
        }
        throw new IllegalArgumentException("Invalid Query Type : "+ type);
    }

    public static String toString(DnsQueryType type) {
        return switch (type) {
            case A -> "IP";
            case AAAA -> "IPV6";
            case NS -> "NS";
            case CName -> "CNAME";
            case MX -> "MX";
        };
    }


}