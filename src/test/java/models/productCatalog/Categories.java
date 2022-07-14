package models.productCatalog;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Categories {
    APPLICATION_INTEGRATION("application_integration"),
    COMPUTE("compute"),
    CONTAINERS("containers"),
    DB("db"),
    DEVOPS_TOOLS("devops_tools"),
    LOGGING("logging"),
    NETWORK("network"),
    OBJECT_STORAGE("object_storage"),
    SECRET_MANAGER("secret_manager"),
    WEB("web"),
    DEFAULT_VALUE(null);
    private final String value;

    Categories(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;

    }

    public static List<String> getCategoriesList() {
        return Stream.of(Categories.values())
                .map(Categories::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return value;
    }
}
