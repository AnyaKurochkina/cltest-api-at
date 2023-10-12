package api.cloud.orderService;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Vault;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import steps.orderService.OrderServiceSteps;
import steps.portalBack.PortalBackSteps;

@Epic("Продукты")
@Feature("Vault")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("vault"), @Tag("prod")})
public class VaultTest extends Tests {

    @TmsLink("1431949")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Создать {0}")
    void create(Vault product) {
        //noinspection EmptyTryBlock
        try (Vault vault = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLinks({@TmsLink("1431950"), @TmsLink("1431951")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Добавить/Удалить право доступа {0}")
    void createRule(Vault product) {
        try (Vault vault = product.createObjectExclusiveAccess()) {
            String accessGroup = PortalBackSteps.getRandomAccessGroup(vault.getProjectId(), "", "vlt");
            vault.addRule(accessGroup, "user-ro", "portal-ro", "user-rw");
            vault.deleteRule(accessGroup);
        }
    }

    @TmsLink("1431952")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Изменить право доступа {0}")
    void editRule(Vault product) {
        try (Vault vault = product.createObjectExclusiveAccess()) {
            String accessGroup = PortalBackSteps.getRandomAccessGroup(vault.getProjectId(),
                    OrderServiceSteps.getDomainByProject(vault.getProjectId()), "vlt");
            vault.addRule(accessGroup, "user-ro", "portal-ro", "user-rw");
            vault.changeRule(accessGroup, "user-ro");
        }
    }

    @TmsLink("1431953")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Удалить {0}")
    @MarkDelete
    void delete(Vault product) {
        try (Vault vault = product.createObjectExclusiveAccess()) {
            vault.deleteObject();
        }
    }
}
