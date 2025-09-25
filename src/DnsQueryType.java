public enum DnsQueryType {
    A(0x0001), // IPv4 address
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
        throw new IllegalArgumentException("Invalid RCode : "+ type);
    }

    public static DnsQueryType fromName(String type) {
        if (type.equals("A")) {
            return A;}
        else if (type.equals("NS")) {
            return NS;
        } else if (type.equals("MX")) {
            return MX;
        }
        throw new IllegalArgumentException("Invalid RCode : "+ type);
    }


}