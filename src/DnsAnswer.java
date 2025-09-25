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
}
