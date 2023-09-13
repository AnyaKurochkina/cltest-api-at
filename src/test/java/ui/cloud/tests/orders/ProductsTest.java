package ui.cloud.tests.orders;

import api.Tests;
import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.authorizer.GlobalUser;
import models.cloud.authorizer.Project;
import models.cloud.authorizer.ProjectEnvironmentPrefix;
import models.cloud.productCatalog.ContextRestrictionsItem;
import models.cloud.productCatalog.InformationSystem;
import models.cloud.productCatalog.ProjectEnvironment;
import models.cloud.productCatalog.product.Product;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.cloud.pages.CloudLoginPage;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.orders.ProductsPage;
import ui.extesions.ConfigExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.portalBack.PortalBackSteps.getInformationSystemId;
import static steps.productCatalog.ProductSteps.createProduct;
import static steps.productCatalog.ProductSteps.partialUpdateProductByName;
import static ui.elements.TypifiedElement.refreshPage;

@Feature("Маркетплейс продуктов для заказа")
@DisabledIfEnv("prod")
@ExtendWith(ConfigExtension.class)
public class ProductsTest extends Tests {

    Project project = Project.builder().isForOrders(true).projectEnvironmentPrefix(ProjectEnvironmentPrefix.byType("DEV"))
            .build().createObject();

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new CloudLoginPage(project.getId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Test
    @TmsLinks({@TmsLink("688514"), @TmsLink("953206")})
    @DisplayName("Открытие продукта и отображение в общем списке маркетплейса")
    void viewOpenProduct() {
        String name = "at_ui_view_open_product";
        Product product = createProduct(name, name);
        ProductsPage page = new IndexPage().clickOrderMore();
        assertFalse(page.isProductDisplayed(product.getTitle()));
        partialUpdateProductByName(name, new JSONObject().put("is_open", "true").put("in_general_list", "true"));
        refreshPage();
        assertTrue(page.isProductDisplayed(product.getTitle()));
        partialUpdateProductByName(name, new JSONObject().put("is_open", "false"));
        refreshPage();
        assertFalse(page.isProductDisplayed(product.getTitle()));
        partialUpdateProductByName(name, new JSONObject().put("is_open", "true").put("in_general_list", "false"));
        refreshPage();
        assertFalse(page.isProductDisplayed(product.getTitle()));
    }

    @Test
    @TmsLink("1140443")
    @DisplayName("Изменение порядка продукта в маркетплейсе")
    void checkProductNumber() {
        String name = "at_ui_check_product_number";
        createProduct(name, name);
        partialUpdateProductByName(name, new JSONObject().put("is_open", "true").put("in_general_list", "true")
                .put("number", "1"));
        ProductsPage page = new IndexPage().clickOrderMore();
        page.expandProductsList();
        int index1 = page.getProducts().indexOf(page.getProducts().find(Condition.exactText(name)));
        partialUpdateProductByName(name, new JSONObject().put("number", "100"));
        refreshPage();
        page.expandProductsList();
        int index2 = page.getProducts().indexOf(page.getProducts().find(Condition.exactText(name)));
        assertTrue(index1 < index2);
    }

    @Test
    @TmsLinks({@TmsLink("1363579"), @TmsLink("1363846")})
    @DisplayName("Ограничение продукта по пользователям")
    void checkProductRestrictionByUsername() {
        String name = "at_ui_role_restriction";
        GlobalUser pcAdmin = GlobalUser.builder().role(Role.PRODUCT_CATALOG_ADMIN).build().createObject();
        GlobalUser orderServiceAdmin = GlobalUser.builder().role(Role.ORDER_SERVICE_ADMIN).build().createObject();
        createProduct(name, name);
        partialUpdateProductByName(name, new JSONObject().put("is_open", "true").put("in_general_list", "true")
                .put("allowed_groups", Collections.singletonList(pcAdmin.getUsername())));
        ProductsPage page = new IndexPage().clickOrderMore();
        assertFalse(page.isProductDisplayed(name));
        partialUpdateProductByName(name, new JSONObject().put("allowed_groups",
                Arrays.asList(pcAdmin.getUsername(), orderServiceAdmin.getUsername())));
        refreshPage();
        assertTrue(page.isProductDisplayed(name));
        partialUpdateProductByName(name, new JSONObject().put("allowed_groups", new JSONArray())
                .put("restricted_groups", Collections.singletonList(pcAdmin.getUsername())));
        refreshPage();
        assertTrue(page.isProductDisplayed(name));
        partialUpdateProductByName(name, new JSONObject().put("restricted_groups",
                Arrays.asList(pcAdmin.getUsername(), orderServiceAdmin.getUsername())));
        refreshPage();
        assertFalse(page.isProductDisplayed(name));
    }

    @Test
    @TmsLinks({@TmsLink("513987"), @TmsLink("1291555")})
    @DisplayName("Ограничение продукта по ролям keycloak")
        //Для учётки должна быть добавлена роль "superadmin-product_catalog" в Keycloak
    void checkProductRestrictionByKeycloakRole() {
        String name = "at_ui_kk_role_restriction";
        String keycloakRole = "superadmin-product_catalog";
        createProduct(name, name);
        partialUpdateProductByName(name, new JSONObject().put("is_open", "true").put("in_general_list", "true")
                .put("allowed_groups", Collections.singletonList(keycloakRole)));
        ProductsPage page = new IndexPage().clickOrderMore();
        assertTrue(page.isProductDisplayed(name));
        partialUpdateProductByName(name, new JSONObject().put("allowed_groups", new JSONArray())
                .put("restricted_groups", Collections.singletonList(keycloakRole)));
        refreshPage();
        assertFalse(page.isProductDisplayed(name));
    }

    @Test
    @TmsLink("806199")
    @DisplayName("Контекстное ограничение по организации")
    void checkContextRestrictionByOrganization() {
        String name = "at_ui_org_context_restriction";
        createProduct(name, name);
        ContextRestrictionsItem restriction1 = ContextRestrictionsItem.builder().organization("org-sandbox").build();
        ContextRestrictionsItem restriction2 = ContextRestrictionsItem.builder().organization("vtb").build();
        partialUpdateProductByName(name, new JSONObject()
                .put("is_open", "true")
                .put("in_general_list", "true")
                .put("context_restrictions", Collections.singletonList(restriction1)));
        ProductsPage page = new IndexPage().clickOrderMore();
        assertFalse(page.isProductDisplayed(name));
        partialUpdateProductByName(name, new JSONObject()
                .put("context_restrictions", Arrays.asList(restriction1, restriction2)));
        refreshPage();
        assertTrue(page.isProductDisplayed(name));
    }

    @Test
    @TmsLink("806286")
    @DisplayName("Контекстное ограничение по информационной системе")
    void checkContextRestrictionByInfSystem() {
        String orgName = "vtb";
        String name = "at_ui_inf_system_context_restriction";
        createProduct(name, name);
        String infSystemId1 = getInformationSystemId(orgName, "grvt");
        String infSystemId2 = project.getInformationSystem();
        ContextRestrictionsItem restriction1 = ContextRestrictionsItem.builder()
                .organization(orgName)
                .information_system(InformationSystem.builder().id(Collections.singletonList(infSystemId1)).build())
                .build();
        ContextRestrictionsItem restriction2 = ContextRestrictionsItem.builder()
                .organization(orgName)
                .information_system(InformationSystem.builder().id(Arrays.asList(infSystemId1, infSystemId2)).build())
                .build();
        partialUpdateProductByName(name, new JSONObject()
                .put("is_open", "true")
                .put("in_general_list", "true")
                .put("context_restrictions", Collections.singletonList(restriction1)));
        ProductsPage page = new IndexPage().clickOrderMore();
        assertFalse(page.isProductDisplayed(name));
        partialUpdateProductByName(name, new JSONObject()
                .put("context_restrictions",
                        Arrays.asList(restriction1, restriction2)));
        refreshPage();
        assertTrue(page.isProductDisplayed(name));
    }

    @Test
    @TmsLink("1224672")
    @DisplayName("Контекстное ограничение по типу среды")
    void checkContextRestrictionByEnvType() {
        String name = "at_ui_env_type_context_restriction";
        createProduct(name, name);
        ContextRestrictionsItem restriction1 = ContextRestrictionsItem.builder()
                .project_environment(ProjectEnvironment.builder()
                        .environment_type(Collections.singletonList("test"))
                        .build())
                .build();
        ContextRestrictionsItem restriction2 = ContextRestrictionsItem.builder()
                .project_environment(ProjectEnvironment.builder()
                        .environment_type(Arrays.asList("dev", "test"))
                        .build())
                .build();
        partialUpdateProductByName(name, new JSONObject()
                .put("is_open", "true")
                .put("in_general_list", "true")
                .put("context_restrictions", Collections.singletonList(restriction1)));
        ProductsPage page = new IndexPage().clickOrderMore();
        assertFalse(page.isProductDisplayed(name));
        partialUpdateProductByName(name, new JSONObject()
                .put("context_restrictions", Arrays.asList(restriction1, restriction2)));
        refreshPage();
        assertTrue(page.isProductDisplayed(name));
    }

    @Test
    @TmsLink("1744128")
    @DisplayName("Контекстное ограничение по среде")
    void checkContextRestrictionByEnv() {
        String name = "at_ui_env_context_restriction";
        createProduct(name, name);
        ContextRestrictionsItem restriction1 = ContextRestrictionsItem.builder()
                .project_environment(ProjectEnvironment.builder()
                        .name(Collections.singletonList("IFT"))
                        .build())
                .build();
        ContextRestrictionsItem restriction2 = ContextRestrictionsItem.builder()
                .project_environment(ProjectEnvironment.builder()
                        .name(Arrays.asList("DEV", "IFT"))
                        .build())
                .build();
        partialUpdateProductByName(name, new JSONObject()
                .put("is_open", "true")
                .put("in_general_list", "true")
                .put("context_restrictions", Collections.singletonList(restriction1)));
        ProductsPage page = new IndexPage().clickOrderMore();
        assertFalse(page.isProductDisplayed(name));
        partialUpdateProductByName(name, new JSONObject()
                .put("context_restrictions", Arrays.asList(restriction1, restriction2)));
        refreshPage();
        assertTrue(page.isProductDisplayed(name));
    }
}
