import java.util.List;

public class DnsAnswer {

    private List<DnsLabel> NAME;
    private DnsQueryType TYPE;
    private int CLASS;
    private long TTL;
    private int RDLENGTH;
    private RData rData;

    public DnsAnswer(List<DnsLabel> name, DnsQueryType type, int classParam, long ttl, int rdLength, RData rData) {
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

        switch (TYPE) {
            case A:

                sb.append("IP\t")
                        .append(rData.getRdata()).append("\t")
                        .append(TTL).append("\t")
                        .append(authFlag);
                break;

            case CName:

                sb.append("CNAME\t")
                        .append(rData.getRdata()).append("\t")
                        .append(TTL).append("\t")
                        .append(authFlag);
                break;

            case MX:
                MXRdata mx = (MXRdata) rData;
                String alias = String.join(".",mx.exchange.stream().map(label -> label.name).toList());
                int pref = mx.preference;
                sb.append("MX\t").append(alias).append("\t").append(pref).append("\t").append(TTL).append("\t").append(authFlag);
                break;

            case NS:
                DomainRdata domain = (DomainRdata) rData;
                sb.append("NS\t")
                        .append(domain.getRdata()).append("\t")
                        .append(TTL).append("\t")
                        .append(authFlag);
                break;

            default:
                sb.append(TYPE.name()).append("\t")
                        .append(domainName).append("\t")
                        .append(TTL).append("\t")
                        .append(authFlag);
        }

        return sb.toString();
    }
}
