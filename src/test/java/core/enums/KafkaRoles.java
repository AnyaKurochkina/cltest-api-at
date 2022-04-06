package core.enums;

public enum KafkaRoles {

    PRODUCER("producer"),
    CONSUMER("consumer");

    public String getRole() {
        return role;
    }

    private final String role;

    KafkaRoles(String role) {
        this.role = role;
    }
}
