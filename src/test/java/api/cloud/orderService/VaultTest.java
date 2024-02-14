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

    private Vault.AppRole getCachedAppRole(Vault vault) {
        return Vault.AppRole.builder()
                .aclCidr("10.10.20.1/16")
                .approleName(vault.fullAppRoleName("cached-app-role"))
                .policy("portal-ro")
                .description("desc")
                .secretIdTtl(0)
                .build();
    }

    @TmsLink("1431949")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Создать {0}")
    void create(Vault product, Integer num) {
        //noinspection EmptyTryBlock
        try (Vault vault = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLinks({@TmsLink("1431950"), @TmsLink("1431951")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Добавить/Удалить право доступа {0}")
    void createRule(Vault product, Integer num) {
        try (Vault vault = product.createObjectExclusiveAccess()) {
            String accessGroup = PortalBackSteps.getRandomAccessGroup(vault.getProjectId(), "", "vlt");
            vault.addRule(accessGroup, "user-ro", "portal-ro", "user-rw");
            vault.deleteRule(accessGroup);
        }
    }

    @TmsLink("1431952")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Изменить право доступа {0}")
    void editRule(Vault product, Integer num) {
        try (Vault vault = product.createObjectExclusiveAccess()) {
            String accessGroup = PortalBackSteps.getRandomAccessGroup(vault.getProjectId(),
                    OrderServiceSteps.getDomainByProject(vault.getProjectId()), "vlt");
            vault.addRule(accessGroup, "user-ro", "portal-ro", "user-rw");
            vault.changeRule(accessGroup, "user-ro");
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Создать AppRole {0}")
    void addAppRole(Vault product, Integer num) {
        try (Vault vault = product.createObjectExclusiveAccess()) {
            vault.addAppRole(getCachedAppRole(vault));
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Перевыпустить ключ {0}")
    void generateSecretIdAppRole(Vault product, Integer num) {
        try (Vault vault = product.createObjectExclusiveAccess()) {
            vault.addAppRole(getCachedAppRole(vault));
            vault.generateSecretIdAppRole(getCachedAppRole(vault));
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удалить AppRole {0}")
    void deleteAppRole(Vault product, Integer num) {
        try (Vault vault = product.createObjectExclusiveAccess()) {
            vault.addAppRole(getCachedAppRole(vault));
            vault.deleteAppRole(getCachedAppRole(vault));
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Изменить AppRole {0}")
    void updateAppRole(Vault product, Integer num) {
        try (Vault vault = product.createObjectExclusiveAccess()) {
            Vault.AppRole appRole = Vault.AppRole.builder()
                    .aclCidr("10.10.20.1/16")
                    .approleName(vault.fullAppRoleName("update-app-role"))
                    .policy("portal-ro")
                    .description("desc")
                    .secretIdTtl(0)
                    .build();

            vault.addAppRole(appRole);
            appRole.setDescription("updated desc");
            vault.updateAppRole(appRole);
        }
    }

    @TmsLink("1431953")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удалить {0}")
    @MarkDelete
    void delete(Vault product, Integer num) {
        try (Vault vault = product.createObjectExclusiveAccess()) {
            vault.deleteObject();
        }
    }
}
