package ui.cloud.tests.productCatalog.action;

import core.enums.Role;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.GlobalUser;
import models.cloud.authorizer.Project;
import models.cloud.authorizer.ProjectEnvironmentPrefix;
import models.cloud.orderService.products.TestProduct;
import models.cloud.productCatalog.ContextRestrictionsItem;
import models.cloud.productCatalog.InformationSystem;
import models.cloud.productCatalog.enums.EventProvider;
import models.cloud.productCatalog.enums.EventType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import ru.testit.annotations.Title;
import ui.cloud.pages.CloudLoginPage;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.orders.OrdersPage;
import ui.cloud.pages.orders.TestProductOrderPage;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;

import static core.helper.StringUtils.format;
import static core.utils.Waiting.findWithRefresh;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static steps.orderService.OrderServiceSteps.registerAction;
import static steps.portalBack.PortalBackSteps.getInformationSystemId;
import static steps.productCatalog.ActionSteps.createAction;
import static steps.productCatalog.ActionSteps.partialUpdateActionByName;

public class ActionRestrictionTest extends ActionBaseTest {

    Project project = Project.builder().isForOrders(true).projectEnvironmentPrefix(ProjectEnvironmentPrefix.byType("DEV"))
            .build().createObject();

    @Override
    @BeforeEach
    @Title("Авторизация на портале")
    public void init() {
        new CloudLoginPage(project.getId())
                .signIn(Role.ORDER_SERVICE_ADMIN);
    }

    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "[{index}] Контекстное ограничение действия по организации")
    @DisplayName("Контекстное ограничение действия по организации")
    public void checkActionContextRestrictionByOrg(TestProduct p) {
        TestProduct product = p.createObject();
        String actionName = "at_ui_org_context_restriction";
        createAction(actionName, EventType.VM, EventProvider.QA_AT);
        registerAction(actionName);
        ContextRestrictionsItem restriction1 = ContextRestrictionsItem.builder().organization("org-sandbox").build();
        ContextRestrictionsItem restriction2 = ContextRestrictionsItem.builder().organization("vtb").build();
        partialUpdateActionByName(actionName, new JSONObject()
                .put("context_restrictions", Collections.singletonList(restriction1)));
        new IndexPage().getOrdersListMenuItem().click();
        new OrdersPage().openOrder(product.getLabel());
        TestProductOrderPage page = new TestProductOrderPage(product);
        assertFalse(page.isActionDisplayed(actionName), format("Действие '{}' отображается", actionName));
        partialUpdateActionByName(actionName, new JSONObject()
                .put("context_restrictions", Arrays.asList(restriction1, restriction2)));
        findWithRefresh(() -> page.isActionDisplayedEnabled(actionName), Duration.ofSeconds(10));
    }

    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "[{index}] Контекстное ограничение действия по информационной системе")
    @DisplayName("Контекстное ограничение действия по информационной системе")
    public void checkActionContextRestrictionByInfSystem(TestProduct p) {
        TestProduct product = p.createObject();
        String actionName = "at_ui_inf_system_context_restriction";
        createAction(actionName, EventType.VM, EventProvider.QA_AT);
        registerAction(actionName);
        String orgName = "vtb";
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
        partialUpdateActionByName(actionName, new JSONObject()
                .put("context_restrictions", Collections.singletonList(restriction1)));
        new IndexPage().getOrdersListMenuItem().click();
        new OrdersPage().openOrder(product.getLabel());
        TestProductOrderPage page = new TestProductOrderPage(product);
        assertFalse(page.isActionDisplayed(actionName), format("Действие '{}' отображается", actionName));
        partialUpdateActionByName(actionName, new JSONObject()
                .put("context_restrictions", Arrays.asList(restriction1, restriction2)));
        findWithRefresh(() -> page.isActionDisplayedEnabled(actionName), Duration.ofSeconds(10));
    }

    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "[{index}] Ограничение действия по параметрам item")
    @DisplayName("Ограничение действия по параметрам item")
    public void checkActionItemRestriction(TestProduct p) {
        TestProduct product = p.createObject();
        String actionName = "at_ui_item_restriction";
        createAction(actionName, EventType.VM, EventProvider.QA_AT);
        registerAction(actionName);
        String restriction1 = "not config['flavor']['name'] == 'c2m4'";
        String restriction2 = "config['flavor']['name'] == 'c2m4'";
        partialUpdateActionByName(actionName, new JSONObject()
                .put("item_restriction", restriction1));
        new IndexPage().getOrdersListMenuItem().click();
        new OrdersPage().openOrder(product.getLabel());
        TestProductOrderPage page = new TestProductOrderPage(product);
        assertFalse(page.isActionDisplayed(actionName), format("Действие '{}' отображается", actionName));
        partialUpdateActionByName(actionName, new JSONObject()
                .put("item_restriction", restriction2));
        findWithRefresh(() -> page.isActionDisplayedEnabled(actionName), Duration.ofSeconds(10));
    }

    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "[{index}] Ограничение действия по ролям")
    @DisplayName("Ограничение действия по ролям")
    //Для учётки, под которой выполняется тест, должна быть добавлена роль "superadmin-product_catalog" в Keycloak
    public void checkActionRoleRestriction(TestProduct p) {
        TestProduct product = p.createObject();
        String actionName = "at_ui_role_restriction";
        createAction(actionName, EventType.VM, EventProvider.QA_AT);
        registerAction(actionName);
        String keycloakRole = "superadmin-product_catalog";
        partialUpdateActionByName(actionName, new JSONObject()
                .put("restricted_groups", Collections.singletonList(keycloakRole)));
        new IndexPage().getOrdersListMenuItem().click();
        new OrdersPage().openOrder(product.getLabel());
        TestProductOrderPage page = new TestProductOrderPage(product);
        assertFalse(page.isActionDisplayed(actionName), format("Действие '{}' отображается", actionName));
        partialUpdateActionByName(actionName, new JSONObject().put("restricted_groups", new JSONArray())
                .put("allowed_groups", Collections.singletonList(keycloakRole)));
        findWithRefresh(() -> page.isActionDisplayedEnabled(actionName), Duration.ofSeconds(10));
    }

    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "[{index}] Ограничение действия по пользователям")
    @DisplayName("Ограничение действия по пользователям")
    public void checkActionUserRestriction(TestProduct p) {
        TestProduct product = p.createObject();
        String actionName = "at_ui_user_restriction";
        createAction(actionName, EventType.VM, EventProvider.QA_AT);
        registerAction(actionName);
        GlobalUser pcAdmin = GlobalUser.builder().role(Role.PRODUCT_CATALOG_ADMIN).build().createObject();
        GlobalUser orderServiceAdmin = GlobalUser.builder().role(Role.ORDER_SERVICE_ADMIN).build().createObject();
        partialUpdateActionByName(actionName, new JSONObject()
                .put("allowed_groups", Collections.singletonList(pcAdmin.getUsername())));
        new IndexPage().getOrdersListMenuItem().click();
        new OrdersPage().openOrder(product.getLabel());
        TestProductOrderPage page = new TestProductOrderPage(product);
        assertFalse(page.isActionDisplayed(actionName), format("Действие '{}' отображается", actionName));
        partialUpdateActionByName(actionName, new JSONObject().put("allowed_groups",
                Arrays.asList(pcAdmin.getUsername(), orderServiceAdmin.getUsername())));
        findWithRefresh(() -> page.isActionDisplayedEnabled(actionName), Duration.ofSeconds(10));
        partialUpdateActionByName(actionName, new JSONObject().put("allowed_groups", new JSONArray())
                .put("restricted_groups", Collections.singletonList(pcAdmin.getUsername())));
        findWithRefresh(() -> page.isActionDisplayedEnabled(actionName), Duration.ofSeconds(10));
        partialUpdateActionByName(actionName, new JSONObject().put("restricted_groups",
                Arrays.asList(pcAdmin.getUsername(), orderServiceAdmin.getUsername())));
        findWithRefresh(() -> !page.isActionDisplayedEnabled(actionName), Duration.ofSeconds(10));
    }

    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "[{index}] Регистрация действия")
    @DisplayName("Регистрация действия")
    @TmsLink("SOUL-6051")
    public void registerActionTest(TestProduct p) {
        TestProduct product = p.createObject();
        String actionName = "at_ui_registered_action";
        createAction(actionName, EventType.VM, EventProvider.QA_AT);
        new IndexPage().getOrdersListMenuItem().click();
        new OrdersPage().openOrder(product.getLabel());
        TestProductOrderPage page = new TestProductOrderPage(product);
        assertFalse(page.isActionDisplayed(actionName), format("Действие '{}' отображается", actionName));
        registerAction(actionName);
        findWithRefresh(() -> page.isActionDisplayedEnabled(actionName), Duration.ofSeconds(10));
    }
}
