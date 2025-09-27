import java.util.List;

public class DnsResourceRecord {
    // Data structure to store and display Record data, this could be an Answer
    // or an additional record

    private List<DnsLabel> NAME;
    private DnsQueryType TYPE;
    private int CLASS;
    private long TTL;
    private int RDLENGTH;
    private RData rData;

    public DnsResourceRecord(List<DnsLabel> name, DnsQueryType type, int classParam, long ttl, int rdLength,
            RData rData) {
        this.NAME = name;
        this.TYPE = type;
        this.CLASS = classParam;
        this.TTL = ttl;
        this.RDLENGTH = rdLength;
        this.rData = rData;
    }

    public String toString(boolean auth) {
        StringBuilder sb = new StringBuilder();
        String domainName = NAME.stream()
                .map(label -> label.name)
                .reduce((a, b) -> a + "." + b)
                .orElse("");

        String authFlag = auth ? "nonauth" : "auth";

        // Handle MX
        if (TYPE == DnsQueryType.MX) {
            MXRdata mx = (MXRdata) rData;
            String alias = String.join(".", mx.exchange.stream().map(label -> label.name).toList());
            int pref = mx.preference;
            sb.append("MX\t").append(alias).append("\t").append(pref).append("\t").append(TTL).append("\t")
                    .append(authFlag);
            // Handle cases for IP addresses
        } else if (TYPE == DnsQueryType.AAAA || TYPE == DnsQueryType.A) {
            sb.append(DnsQueryType.toString(TYPE)).append("\t").append(rData.getRdata()).append("\t").append(TTL)
                    .append("\t").append(authFlag);
        } else {
            sb.append(DnsQueryType.toString(TYPE)).append("\t").append(domainName).append("\t").append(TTL).append("\t")
                    .append(authFlag);
        }

        return sb.toString();
    }
}
