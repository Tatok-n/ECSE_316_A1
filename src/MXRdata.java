import java.util.List;

public class MXRdata implements RData {
    int preference;
    List<DnsLabel> exchange;

    @Override
    public Object getRdata() {
        return null;
    }

    public MXRdata(int preference, List<DnsLabel> exchange) {
        this.preference = preference;
        this.exchange = exchange;
    }
}
