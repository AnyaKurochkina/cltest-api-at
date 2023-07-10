package models.cloud.productCatalog.product;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Categories {
    APPLICATION_INTEGRATION("application_integration"),
    AI_CLOUD("ai_cloud"),
    COMPUTE("compute"),
    COMPUTE_IP("compute-ip"),
    COMPUTE_NIC("compute-nic"),
    COMPUTE_VIP("compute-vip"),
    CONTAINER_KUBERNETES("container_kubernetes"),
    COMPUTE_BACKUP("compute-backup"),
    COMPUTE_VOLUME("compute-volume"),
    COMPUTE_INSTANCE("compute-instance"),
    CLOUD_DNS("cloud_dns"),
    CONTAINER("container"),
    CONTAINERS("containers"),
    CRAAS("craas"),
    DB("db"),
    FAAS_FUNCTIONS("faas_functions"),
    DEVOPS_TOOLS("devops_tools"),
    LOGGING("logging"),
    NETWORK("network"),
    OBJECT_STORAGE("object_storage"),
    PRODUCT_TEMPLATE("product_template"),
    SECRET_MANAGER("secret_manager"),
    STREAMING("streaming"),
    STRONGSWAN("strongswan"),
    MANAGED_SERVICE("managed_service"),
    WEB("web"),
    SAAS("saas"),
    RPA("rpa"),
    QA_TEST("qa_test"),
    OMNI_SERVICES("omni_services"),
    COMPLEX_REQUEST("complex_request"),
    T1_DISK("t1_disk"),
    COMPUTE_IMAGE("compute-image"),
    COMPUTE_SNAPSHOT("compute-snapshot"),
    CDN("cdn"),
    VM("vm"),
    VMWARE_VDC("vmware_vdc");

    private final String value;

    Categories(final String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;

    }

    public static List<String> getCategoriesList() {
        return Stream.of(Categories.values())
                .map(Categories::getValue)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return value;
    }
}
