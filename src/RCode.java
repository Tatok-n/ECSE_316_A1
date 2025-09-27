public enum RCode {
    // record error codes
    NoError(0),
    FormatError(1),
    ServerFailure(2),
    NameError(3),
    NotImplemented(4),
    Refused(5);

    public final int value;

    RCode(int value) {
        this.value = value;
    }

    public static RCode fromValue(int code) {
        for (RCode rc : RCode.values()) {
            if (rc.value == code) {
                return rc;
            }
        }
        throw new IllegalArgumentException("Invalid RCode : " + code);
    }
}
