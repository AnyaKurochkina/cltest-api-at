package ui.cloud.tests.orders;

import api.Tests;
import com.codeborne.selenide.Condition;
import core.enums.Role;
import core.utils.Waiting;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.authorizer.GlobalUser;
import models.cloud.authorizer.Project;
import models.cloud.authorizer.ProjectEnvironmentPrefix;
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
import static steps.productCatalog.ProductSteps.createProduct;
import static steps.productCatalog.ProductSteps.partialUpdateProductByName;
import static ui.elements.TypifiedElement.refresh;

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
        refresh();
        assertTrue(page.isProductDisplayed(product.getTitle()));
        partialUpdateProductByName(name, new JSONObject().put("is_open", "false"));
        refresh();
        assertFalse(page.isProductDisplayed(product.getTitle()));
        partialUpdateProductByName(name, new JSONObject().put("is_open", "true").put("in_general_list", "false"));
        refresh();
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
        refresh();
        page.expandProductsList();
        int index2 = page.getProducts().indexOf(page.getProducts().find(Condition.exactText(name)));
        assertTrue(index1 < index2);
    }

    @Test
    @TmsLinks({@TmsLink("1363579"), @TmsLink("1363846")})
    @DisplayName("Ограничение продукта по пользователям")
    void checkProductRestrictionByUsername() {
        String name = "at_ui_check_product_role_restriction";
        GlobalUser pcAdmin = GlobalUser.builder().role(Role.PRODUCT_CATALOG_ADMIN).build().createObject();
        GlobalUser orderServiceAdmin = GlobalUser.builder().role(Role.ORDER_SERVICE_ADMIN).build().createObject();
        createProduct(name, name);
        partialUpdateProductByName(name, new JSONObject().put("is_open", "true").put("in_general_list", "true")
                .put("allowed_groups", Collections.singletonList(pcAdmin.getUsername())));
        ProductsPage page = new IndexPage().clickOrderMore();
        page.expandProductsList();
        assertFalse(page.isProductDisplayed(name));
        partialUpdateProductByName(name, new JSONObject().put("allowed_groups",
                Arrays.asList(pcAdmin.getUsername(), orderServiceAdmin.getUsername())));
        refresh();
        page.expandProductsList();
        assertTrue(page.isProductDisplayed(name));
        partialUpdateProductByName(name, new JSONObject().put("allowed_groups", new JSONArray())
                .put("restricted_groups", Collections.singletonList(pcAdmin.getUsername())));
        refresh();
        page.expandProductsList();
        assertTrue(page.isProductDisplayed(name));
        partialUpdateProductByName(name, new JSONObject().put("restricted_groups",
                Arrays.asList(pcAdmin.getUsername(), orderServiceAdmin.getUsername())));
        refresh();
        page.expandProductsList();
        assertFalse(page.isProductDisplayed(name));
    }

    @Test
    @TmsLinks({@TmsLink("513987"), @TmsLink("1291555")})
    @DisplayName("Ограничение продукта по ролям keycloak")
        //Для учётки должна быть добавлена роль "superadmin-product_catalog" в Keycloak
    void checkProductRestrictionByKeycloakRole() {
        String name = "at_ui_check_product_kk_role_restriction";
        String keycloakRole = "superadmin-product_catalog";
        createProduct(name, name);
        partialUpdateProductByName(name, new JSONObject().put("is_open", "true").put("in_general_list", "true")
                .put("allowed_groups", Collections.singletonList(keycloakRole)));
        ProductsPage page = new IndexPage().clickOrderMore();
        page.expandProductsList();
        assertTrue(page.isProductDisplayed(name));
        partialUpdateProductByName(name, new JSONObject().put("allowed_groups", new JSONArray())
                .put("restricted_groups", Collections.singletonList(keycloakRole)));
        refresh();
        page.expandProductsList();
        assertFalse(page.isProductDisplayed(name));
    }
}
