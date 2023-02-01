package api.cloud.orderService;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Astra;
import models.cloud.orderService.products.Vault;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import steps.orderService.OrderServiceSteps;
import steps.portalBack.PortalBackSteps;

@Epic("Продукты")
@Feature("Vault")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("vault"), @Tag("prod")})
public class VaultTest extends Tests {

//    @TmsLink("391703")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Vault product) {
        //noinspection EmptyTryBlock
        try (Vault vault = product.createObjectExclusiveAccess()) {
        }
    }

//    @TmsLink("391703")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить/Удалить право доступа {0}")
    void createRule(Vault product) {
        try (Vault vault = product.createObjectExclusiveAccess()) {
            String accessGroup = PortalBackSteps.getRandomAccessGroup(vault.getProjectId(), "", "vlt");
            vault.addRule(accessGroup, "user-ro", "portal-ro", "user-rw");
            vault.deleteRule(accessGroup);
        }
    }

    //    @TmsLink("391703")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить право доступа {0}")
    void editRule(Vault product) {
        try (Vault vault = product.createObjectExclusiveAccess()) {
            String accessGroup = PortalBackSteps.getRandomAccessGroup(vault.getProjectId(), "", "vlt");
            vault.addRule(accessGroup, "user-ro", "portal-ro", "user-rw");
            vault.changeRule(accessGroup, "user-ro");
        }
    }

//    @TmsLink("391698")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(Vault product) {
        try (Vault vault = product.createObjectExclusiveAccess()) {
            vault.deleteObject();
        }
    }
}
