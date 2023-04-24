package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import ui.cloud.pages.productCatalog.AuditPage;
import ui.cloud.pages.productCatalog.ContextSettingsPage;
import ui.cloud.pages.productCatalog.actions.ActionsListPage;
import ui.cloud.pages.productCatalog.allowedAction.AllowedActionsListPage;
import ui.cloud.pages.productCatalog.forbiddenAction.ForbiddenActionsListPage;
import ui.cloud.pages.productCatalog.graph.GraphsListPage;
import ui.cloud.pages.productCatalog.jinja2Template.Jinja2TemplatesListPage;
import ui.cloud.pages.productCatalog.orderTemplate.OrderTemplatesListPage;
import ui.cloud.pages.productCatalog.orgDirectionsPages.OrgDirectionsListPage;
import ui.cloud.pages.productCatalog.product.ProductsListPage;
import ui.cloud.pages.productCatalog.service.ServicesListPagePC;
import ui.cloud.pages.productCatalog.template.TemplatesListPage;
import ui.elements.Select;

import static com.codeborne.selenide.Selenide.$x;

@Getter
public class ControlPanelIndexPage {

    private final SelenideElement mainPageMenuItem = $x("//div[text()='Главная']");
    private final SelenideElement graphsMenuItem = $x("//div[text()='Графы']");
    private final SelenideElement directionsMenuItem = $x("//div[text()='Направления']");
    private final SelenideElement actionsMenuItem = $x("//div[text()='Действия']");
    private final SelenideElement forbiddenActionsMenuItem = $x("//div[text()='Запрещенные действия']");
    private final SelenideElement allowedActionsMenuItem = $x("//div[text()='Разрешенные действия']");
    private final SelenideElement templatesMenuItem = $x("//div[text()='Шаблоны узлов']");
    private final SelenideElement jinja2TemplatesMenuItem = $x("//div[text()='Шаблоны Jinja2']");
    private final SelenideElement orderTemplatesMenuItem = $x("//div[text()='Шаблоны отображения']");
    private final SelenideElement servicesMenuItem = $x("//div[text()='Сервисы']");
    private final SelenideElement productsMenuItem = $x("//div[text()='Продукты']");
    private final SelenideElement utilsMenuItem = $x("//div[text()='Утилиты']");
    private final SelenideElement auditMenuItem = $x("//div[text()='Аудит']");
    private final SelenideElement contextSettingsMenuItem = $x("//div[text()='Настройки контекста']");
    private final SelenideElement mainLogo = $x("//img");

    public ControlPanelIndexPage() {
        mainLogo.hover();
        mainPageMenuItem.shouldBe(Condition.visible);
    }

    @Step("Переход на страницу Конструктор. Графы")
    public GraphsListPage goToGraphsPage() {
        graphsMenuItem.click();
        return new GraphsListPage();
    }

    @Step("Переход на страницу Конструктор. Направления")
    public OrgDirectionsListPage goToOrgDirectionsPage() {
        directionsMenuItem.click();
        return new OrgDirectionsListPage();
    }

    @Step("Переход на страницу Конструктор. Действия")
    public ActionsListPage goToActionsListPage() {
        actionsMenuItem.click();
        return new ActionsListPage();
    }

    @Step("Переход на страницу Конструктор. Запрещенные действия")
    public ForbiddenActionsListPage goToForbiddenActionsListPage() {
        forbiddenActionsMenuItem.click();
        return new ForbiddenActionsListPage();
    }

    @Step("Переход на страницу Конструктор. Разрешенные действия")
    public AllowedActionsListPage goToAllowedActionsListPage() {
        allowedActionsMenuItem.click();
        return new AllowedActionsListPage();
    }

    @Step("Переход на страницу Конструктор. Шаблоны узлов")
    public TemplatesListPage goToTemplatesPage() {
        templatesMenuItem.click();
        return new TemplatesListPage();
    }

    @Step("Переход на страницу Конструктор. Шаблоны Jinja2")
    public Jinja2TemplatesListPage goToJinja2TemplatesListPage() {
        jinja2TemplatesMenuItem.click();
        return new Jinja2TemplatesListPage();
    }

    @Step("Переход на страницу Конструктор. Шаблоны отображения")
    public OrderTemplatesListPage goToOrderTemplatesPage() {
        orderTemplatesMenuItem.click();
        return new OrderTemplatesListPage();
    }

    @Step("Переход на страницу Конструктор. Сервисы")
    public ServicesListPagePC goToServicesListPagePC() {
        servicesMenuItem.click();
        return new ServicesListPagePC();
    }

    @Step("Переход на страницу Конструктор. Продукты")
    public ProductsListPage goToProductsListPage() {
        productsMenuItem.click();
        return new ProductsListPage();
    }

    @Step("Переход на страницу Утилиты. Аудит")
    public AuditPage goToAuditPage() {
        utilsMenuItem.click();
        auditMenuItem.click();
        Waiting.sleep(500);
        return new AuditPage();
    }

    @Step("Переход на страницу 'Настройки контекста'")
    public ContextSettingsPage goToContextSettingsPage() {
        contextSettingsMenuItem.click();
        return new ContextSettingsPage();
    }
}