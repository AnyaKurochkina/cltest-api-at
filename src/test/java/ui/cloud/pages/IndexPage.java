package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import core.helper.StringUtils;
import io.qameta.allure.Step;
import lombok.Getter;
import ui.cloud.pages.productCatalog.actions.ActionsListPage;
import ui.cloud.pages.productCatalog.graph.GraphsListPage;
import ui.cloud.pages.productCatalog.orgDirectionsPages.OrgDirectionsListPage;
import ui.cloud.pages.productCatalog.template.TemplatesListPage;
import ui.cloud.pages.services.ServicesListPage;

import static com.codeborne.selenide.Selenide.$x;
import static tests.Tests.activeCnd;
import static tests.Tests.clickableCnd;

@Getter
public class IndexPage {
    final SelenideElement linkServicesList = StringUtils.$x("//a[.='Список сервисов']");
    private final SelenideElement orderMoreBtn = $x("//button[contains(., 'Заказать еще')]");
    private final SelenideElement btnProducts = Selenide.$x("//div[not(@hidden)]/a[@href='/vm/orders' and text()='Продукты']");
    private final SelenideElement graphs = $x("//*[@href='/meccano/graphs']");
    private final SelenideElement directions = $x("//*[@href='/meccano/org_direction']");
    private final SelenideElement actions = $x("//*[@href='/meccano/actions']");
    private final SelenideElement templates = $x("//a[@href='/meccano/templates']");

    public ProductsPage clickOrderMore() {
        orderMoreBtn.shouldBe(Condition.visible).shouldBe(Condition.enabled).hover().click();
        return new ProductsPage();
    }

    @Step("Переход на страницу Конструктор.Графы")
    public GraphsListPage goToGraphsPage() {
        graphs.click();
        return new GraphsListPage();
    }

    @Step("Переход на страницу Конструктор.Направления")
    public OrgDirectionsListPage goToOrgDirectionsPage() {
        directions.scrollTo();
        directions.click();
        return new OrgDirectionsListPage();
    }

    @Step("Переход на страницу Конструктор.Действия")
    public ActionsListPage goToActionsPage() {
        actions.scrollTo();
        actions.click();
        return new ActionsListPage();
    }

    @Step("Переход на страницу 'Список сервисов'")
    public ServicesListPage goToServicesListPage() {
        linkServicesList.shouldBe(activeCnd).shouldBe(clickableCnd).hover().click();
        return new ServicesListPage();
    }

    @Step("Переход на страницу Конструктор.Шаблоны узлов")
    public TemplatesListPage goToTemplatesPage() {
        templates.click();
        return new TemplatesListPage();
    }
}