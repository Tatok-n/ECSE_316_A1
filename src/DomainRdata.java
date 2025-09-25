import java.util.ArrayList;
import java.util.List;

public class DomainRdata implements RData{
    private List<DnsLabel> labels;
    @Override
    public String getRdata() {
        return String.join(".",(labels.stream().map(label -> label.name).toList()));
    }

    public DomainRdata(List<DnsLabel> labels){
        this.labels = labels;
    }
}
