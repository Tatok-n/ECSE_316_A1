import java.util.List;

public class DnsQuestion {
    private List<DnsLabel> QName;
    private DnsQueryType qType;
    private int QCLASS = 0x0001;


    public DnsQuestion(List<DnsLabel> labels, DnsQueryType type, int qClass) {
        this.QName = labels;
        this.qType = type;
        this.QCLASS = qClass;
    }
}
