package models.cloud.productCatalog.product;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Categories {
    APPLICATION_INTEGRATION("application_integration"),
    COMPUTE("compute"),
    CONTAINER("container"),
    CONTAINERS("containers"),
    DB("db"),
    DEVOPS_TOOLS("devops_tools"),
    LOGGING("logging"),
    NETWORK("network"),
    OBJECT_STORAGE("object_storage"),
    SECRET_MANAGER("secret_manager"),
    STREAMING("streaming"),
    WEB("web"),
    SAAS("saas"),
    QA_TEST("qa_test"),
    OMNI_SERVICES("omni_services"),
    PRODUCT_TEMPLATE("product_template"),
    COMPLEX_REQUEST("complex_request"),
    VM("vm");

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
