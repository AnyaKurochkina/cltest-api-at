package models.cloud.productCatalog.enums;

public enum EventProvider {
    S3("s3"),
    HCP("hcp"),
    CEPH("ceph"),
    MOON("moon"),
    TAAS("taas"),
    VSPHERE("vsphere"),
    OPENSTACK("openstack"),
    WILDFLY("wildfly");

    private final String value;

    EventProvider(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}