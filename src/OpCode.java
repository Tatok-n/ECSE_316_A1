public class OpCode {
    boolean AA;
    boolean TC;
    boolean RD;
    boolean RA;
    int Z; // 3 bits

    public OpCode(boolean AA, boolean TC, boolean RD, boolean RA) {
        this.AA = AA;
        this.TC = TC;
        this.RD = RD;
        this.RA = RA;
        this.Z = 0;
    }


}
