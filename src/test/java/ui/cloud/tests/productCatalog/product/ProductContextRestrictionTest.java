package ui.cloud.tests.productCatalog.product;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.ContextRestrictionsItem;
import models.cloud.productCatalog.ProjectEnvironment;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.RestrictionsPage;
import ui.cloud.pages.productCatalog.product.ProductPage;
import ui.elements.Alert;
import ui.elements.Table;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.ProductSteps.partialUpdateProductByName;

@Feature("Контекстное ограничение продукта")
public class ProductContextRestrictionTest extends ProductBaseTest {

    @Test
    @TmsLink("1733614")
    @DisplayName("Добавить контекстное ограничение")
    public void addContextRestriction() {
        String org = "org-sandbox", infSystem = "crux", criticalCategory = "Other";
        new ControlPanelIndexPage()
                .goToProductsListPage()
                .findAndOpenProductPage(NAME);
        new ProductPage().getRestrictionsTab().switchTo();
        RestrictionsPage page = new RestrictionsPage();
        page.getAddContextRestrictionButton().click();
        page.getInfSystemSelect().getElement().$x("select").shouldBe(Condition.disabled);
        page.getOrgSelect().set(org);
        page.getInfSystemSelect().setContains(infSystem);
        page.getCriticalCategorySelect().set(criticalCategory);
        page.getEnvTypeSelect().set("dev", "test");
        page.getEnvSelect().set("DEV", "IFT");
        page.getAddButton().click();
        Alert.green("Контекстное ограничение добавлено. Сохраните объект");
        page.saveWithoutPatchVersion("Продукт успешно изменен");
        page.checkContextRestrionsRecord(org, infSystem, criticalCategory,
                "dev\ntest", "DEV\nIFT");
    }

    @Test
    @TmsLink("807227")
    @DisplayName("Удалить контекстное ограничение")
    public void deleteContextRestriction() {
        ContextRestrictionsItem restriction = ContextRestrictionsItem.builder()
                .organization("org-sandbox")
                .project_environment(ProjectEnvironment.builder()
                        .environment_type(Collections.singletonList("dev"))
                        .name(Collections.singletonList("DEV"))
                        .build())
                .build();
        partialUpdateProductByName(NAME, new JSONObject()
                .put("context_restrictions",
                        Collections.singletonList(restriction)));
        new ControlPanelIndexPage()
                .goToProductsListPage()
                .findAndOpenProductPage(NAME);
        new ProductPage().getRestrictionsTab().switchTo();
        RestrictionsPage page = new RestrictionsPage();
        page.deleteRestriction().saveWithoutPatchVersion("Продукт успешно изменен");
        assertTrue(new Table("Критичность ИС").isEmpty());
    }

    @Test
    @TmsLink("1745130")
    @DisplayName("Редактировать контекстное ограничение")
    public void editContextRestriction() {
        ContextRestrictionsItem restriction = ContextRestrictionsItem.builder()
                .organization("org-sandbox")
                .project_environment(ProjectEnvironment.builder()
                        .environment_type(Collections.singletonList("dev"))
                        .name(Collections.singletonList("DEV"))
                        .build())
                .build();
        partialUpdateProductByName(NAME, new JSONObject()
                .put("context_restrictions",
                        Collections.singletonList(restriction)));
        new ControlPanelIndexPage()
                .goToProductsListPage()
                .findAndOpenProductPage(NAME);
        new ProductPage().getRestrictionsTab().switchTo();
        RestrictionsPage page = new RestrictionsPage();
        page.openEditDialog();
        page.getOrgSelect().set("vtb");
        page.getEnvTypeSelect().set("dev", "test", "prod");
        page.getEnvSelect().set("DEV", "IFT");
        page.getSaveButton().click();
        Alert.green("Контекстное ограничение отредактировано. Сохраните объект");
        page.checkContextRestrionsRecord("vtb", "", "",
                "dev\nprod\ntest", "DEV\nIFT");
    }
}
