public class DnsNotFoundException extends RuntimeException {
    // custom error for not found requests
    public DnsNotFoundException(String message) {
        super(message);
    }
}