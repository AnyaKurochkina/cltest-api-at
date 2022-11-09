package models.cloud.productCatalog.enums;

public enum EventType {
    BM("bm"),
    S3("s3"),
    VM("vm"),
    ACL("acl"),
    APP("app"),
    VDC("vdc"),
    PAAS("paas"),
    CLUSTER("cluster");

    private final String value;

    EventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
