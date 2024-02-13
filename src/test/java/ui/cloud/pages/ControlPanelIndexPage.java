package ui.cloud.pages;

import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import ui.cloud.pages.productCatalog.AuditPage;
import ui.cloud.pages.productCatalog.ContextSettingsPage;
import ui.cloud.pages.productCatalog.MeccanoAuditPage;
import ui.cloud.pages.productCatalog.actions.ActionsListPage;
import ui.cloud.pages.productCatalog.allowedAction.AllowedActionsListPage;
import ui.cloud.pages.productCatalog.forbiddenAction.ForbiddenActionsListPage;
import ui.cloud.pages.productCatalog.graph.GraphsListPage;
import ui.cloud.pages.productCatalog.jinja2Template.Jinja2TemplatesListPage;
import ui.cloud.pages.productCatalog.orderTemplate.OrderTemplatesListPage;
import ui.cloud.pages.productCatalog.orgDirectionsPages.OrgDirectionsListPage;
import ui.cloud.pages.productCatalog.product.ProductsListPage;
import ui.cloud.pages.productCatalog.service.ServicesListPagePC;
import ui.cloud.pages.productCatalog.tag.TagsListPage;
import ui.cloud.pages.productCatalog.template.TemplatesListPage;
import ui.elements.Button;
import ui.elements.Tab;

import static com.codeborne.selenide.Selenide.$x;

@Getter
public class ControlPanelIndexPage {

    private final SelenideElement graphsLink = $x("//*[@href='/meccano/graphs']");
    private final SelenideElement directionsLink = $x("//*[@href='/meccano/org_direction']");
    private final SelenideElement actionsLink = $x("//*[@href='/meccano/actions']");
    private final SelenideElement forbiddenActionsLink = $x("//*[@href='/meccano/forbidden_actions']");
    private final SelenideElement allowedActionsLink = $x("//*[@href='/meccano/allowed_actions']");
    private final SelenideElement templatesLink = $x("//a[@href='/meccano/templates']");
    private final SelenideElement jinja2TemplatesLink = $x("//a[@href='/meccano/jinja2-templates']");
    private final SelenideElement orderTemplatesLink = $x("//a[@href='/meccano/order-templates']");
    private final SelenideElement servicesLink = $x("//a[@href='/meccano/services']");
    private final SelenideElement productsLink = $x("//a[@href='/meccano/products']");
    private final SelenideElement auditLink = $x("//a[@href='/day2/audit']");
    private final SelenideElement contextSettingsLink = $x("//a[@href='/meccano/context_settings']");
    private final Button mainMenuButton = Button.byXpath("//div[contains(@class, 'MainMenu')]//button");
    private final SelenideElement utilsMenuItem = $x("//div[text()='Утилиты']");
    private final SelenideElement accountSettingsMenuItem = $x("//div[text()='Настройки аккаунта']");
    private final SelenideElement tagsMenuItem = $x("//div[text()='Теги']");
    private final SelenideElement meccanoAuditMenuItem = $x("//div[text()='История изменений']");

    @Step("Переход на страницу Конструктор.Графы")
    public GraphsListPage goToGraphsPage() {
        graphsLink.click();
        return new GraphsListPage();
    }

    @Step("Переход на страницу Конструктор. Направления")
    public OrgDirectionsListPage goToOrgDirectionsPage() {
        directionsLink.click();
        return new OrgDirectionsListPage();
    }

    @Step("Переход на страницу Конструктор. Действия")
    public ActionsListPage goToActionsListPage() {
        actionsLink.click();
        return new ActionsListPage();
    }

    @Step("Переход на страницу Конструктор. Запрещенные действия")
    public ForbiddenActionsListPage goToForbiddenActionsListPage() {
        forbiddenActionsLink.click();
        return new ForbiddenActionsListPage();
    }

    @Step("Переход на страницу Конструктор. Разрешенные действия")
    public AllowedActionsListPage goToAllowedActionsListPage() {
        allowedActionsLink.click();
        return new AllowedActionsListPage();
    }

    @Step("Переход на страницу Конструктор.Шаблоны узлов")
    public TemplatesListPage goToTemplatesPage() {
        templatesLink.click();
        return new TemplatesListPage();
    }

    @Step("Переход на страницу Конструктор. Шаблоны Jinja2")
    public Jinja2TemplatesListPage goToJinja2TemplatesListPage() {
        jinja2TemplatesLink.click();
        return new Jinja2TemplatesListPage();
    }

    @Step("Переход на страницу Конструктор. Шаблоны отображения")
    public OrderTemplatesListPage goToOrderTemplatesPage() {
        orderTemplatesLink.click();
        return new OrderTemplatesListPage();
    }

    @Step("Переход на страницу Конструктор. Сервисы")
    public ServicesListPagePC goToServicesListPagePC() {
        servicesLink.click();
        return new ServicesListPagePC();
    }

    @Step("Переход на страницу Конструктор. Продукты")
    public ProductsListPage goToProductsListPage() {
        productsLink.click();
        return new ProductsListPage();
    }

    @Step("Переход на страницу Утилиты. Аудит")
    public AuditPage goToAuditPage() {
        mainMenuButton.click();
        utilsMenuItem.hover();
        auditLink.click();
        Waiting.sleep(500);
        return new AuditPage();
    }

    @Step("Переход на страницу 'Настройки контекста'")
    public ContextSettingsPage goToContextSettingsPage() {
        accountSettingsMenuItem.click();
        Tab.byText("Настройки контекста").switchTo();
        return new ContextSettingsPage();
    }

    @Step("Переход в раздел 'Теги'")
    public TagsListPage goToTagsPage() {
        tagsMenuItem.click();
        return new TagsListPage();
    }

    @Step("Переход на страницу Конструктор. История изменений")
    public MeccanoAuditPage goToMeccanoAuditPage() {
        meccanoAuditMenuItem.click();
        return new MeccanoAuditPage();
    }
}