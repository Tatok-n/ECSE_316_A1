public class DnsLabel {
    byte length;
    String name;

    public DnsLabel(byte length, String name) {
        if (length > 63) {
            throw new IllegalArgumentException("Cannot have more than 63 octets in a label !");
        }
        this.length = length;
        this.name = name;
    }
}
