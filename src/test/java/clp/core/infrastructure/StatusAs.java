package clp.core.infrastructure;


public enum StatusAs {
    NO_RUN("No Run"),
    PASSED("Passed"),
    FAILED("Failed"),
    BLOCKED("Blocked"),
    N_A("N/A"),
    NOT_COMPLETED("Not Completed");

    private String status;

    private StatusAs(String status) {
        this.setStatus(status);
    }

    public String getStatus() {
        return this.status;
    }

    private void setStatus(String status) {
        this.status = status;
    }

    public static void main(String[] args) {
        System.out.println(NOT_COMPLETED.getStatus());
        System.out.println(NO_RUN);
    }
}
