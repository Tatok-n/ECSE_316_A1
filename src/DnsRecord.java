import java.util.ArrayList;
import java.util.List;

public class DnsRecord {
    // Data structure to store and print received records

    // Header
    private int id;
    private boolean QR;
    private OpCode opCode;
    private RCode rCode;

    private int QDCOUNT = 1;
    private int ANCOUNT;
    private int NSCOUNT;
    private int ARCOUNT;

    //Records
    private List<DnsQuestion> questions = new ArrayList<>();
    private List<DnsRR> answers = new ArrayList<>();
    private List<DnsRR> additionalRecords = new ArrayList<>();

    public void setHeader(int id, boolean QR, OpCode opCode, RCode rCode, int qdCount,int anCount,int nsCount,int arCount ) {
        this.id = id;
        this.QR = QR;
        this.opCode = opCode;
        this.rCode = rCode;
        this.QDCOUNT = qdCount;
        this.ANCOUNT = anCount;
        this.NSCOUNT = nsCount;
        this.ARCOUNT = arCount;
    }

    public int getNumberOfAnswers() {
        return ANCOUNT;
    }

    public int getNumberOfAdditionalRecords() {
        return ARCOUNT;
    }

    public boolean isAuth() {
        return opCode.AA;
    }

    public void addQuestion(List<DnsLabel> labels, DnsQueryType type, int qClass) {
        DnsQuestion question = new DnsQuestion(labels, type, qClass);
        questions.add(question);
    }

    public void addAnswer(DnsRR answer) {
        answers.add(answer);
    }

    public void addAdditionalRR(DnsRR additionalRecord) {
        additionalRecords.add(additionalRecord);
    }

    public List<DnsRR> getAnswers() {
        return answers;
    }

    public List<DnsRR> getAdditionalRecords() {
        return additionalRecords;
    }

}
