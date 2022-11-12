package ui.cloud.pages;

import com.codeborne.selenide.*;
import core.exception.CreateEntityException;
import core.helper.StringUtils;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import models.cloud.orderService.interfaces.IProduct;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebElement;
import steps.stateService.StateServiceSteps;
import ui.cloud.tests.ActionParameters;
import ui.elements.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;

import static com.codeborne.selenide.Selenide.open;
import static core.helper.StringUtils.$$x;
import static core.helper.StringUtils.$x;
import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static ui.elements.TypifiedElement.scrollCenter;

@Log4j2
@Getter
public abstract class IProductPage {
    IProduct product;
    Double preBillingCostAction;
    SelenideElement productName = $x("(//button[@title='Редактировать']/ancestor::*/span)[1]");

    abstract void checkPowerStatus(String expectedStatus);

    SelenideElement btnHistory = $x("//button[.='История действий']");
    SelenideElement btnGeneralInfo = $x("//button[.='Общая информация']");
    SelenideElement generatePassButton = $x("//button[@aria-label='generate']");
    SelenideElement noData = Selenide.$x("//*[text() = 'Нет данных для отображения']");

    private final SelenideElement currentPriceOrder = Selenide.$x("(//p[contains(.,'₽/сут.') and contains(.,',')])[1]");
    private final SelenideElement preBillingPriceAction = Selenide.$x("//div[contains(.,'Новая стоимость услуги')]/descendant::p[contains(.,'₽/сут.') and contains(.,',')]");

    public IProductPage(IProduct product) {
        if (Objects.nonNull(product.getError()))
            throw new CreateEntityException(String.format("Продукт необходимый для выполнения теста был создан с ошибкой:\n%s", product.getError()));
        if (Objects.nonNull(product.getLink()))
            TypifiedElement.open(product.getLink());
        btnGeneralInfo.shouldBe(Condition.enabled);
        product.setLink(WebDriverRunner.getWebDriver().getCurrentUrl());
        product.addLinkProduct();
        this.product = product.buildFromLink();
    }

    public void waitChangeStatus() {
        EntitiesUtils.waitChangeStatus(new TopInfo(), Duration.ofMinutes(8));
    }

    public void waitChangeStatus(Duration duration) {
        EntitiesUtils.waitChangeStatus(new TopInfo(), duration);
    }

    @Step("Переключение 'Защита от удаления' в состояние '{expectValue}'")
    public void switchProtectOrder(String expectValue) {
        ProductStatus status = new ProductStatus(expectValue);
        runActionWithParameters(getLabel(), "Защита от удаления", "Подтвердить",
                () -> Input.byLabel("Включить защиту от удаления").click(), ActionParameters.builder().waitChangeStatus(false).checkPreBilling(false).checkLastAction(false).build());
        new TopInfo().getValueByColumnInFirstRow("Защита от удаления").$x("descendant::*[name()='svg']")
                .shouldBe(Condition.match(expectValue, e -> new ProductStatus(e).equals(status)), Duration.ofSeconds(10));
    }

    public SelenideElement getBtnAction(String header) {
        return $x("//ancestor::*[.='{}']/parent::*//button[@id='actions-menu-button']", header);
    }

    @Step("Получение таблицы по заголовку")
    public Table getTableByHeader(String header) {
        return new Table($$x("//ancestor::*[.='{}']/parent::*//table", header).filter(Condition.visible).first());
    }

    @Step("Получение label")
    public String getLabel() {
        return $x("//span[starts-with(text(),'AT-UI-')]").shouldBe(Condition.visible).getText();
    }

    @Step("Запуск действия '{action}'")
    protected void runActionWithoutParameters(SelenideElement button, String action, ActionParameters params) {
        String productNameText = null;
        //btnGeneralInfo.scrollIntoView(scrollCenter).shouldBe(Condition.enabled).click();
        if(Objects.nonNull(params.getNode())){
            productNameText = productName.getText();
            params.getNode().scrollIntoView(scrollCenter).click();
        }
        button.shouldBe(activeCnd).scrollIntoView(scrollCenter).hover().shouldBe(clickableCnd).click();
        $$x("//li[.='{}']", action).shouldBe(CollectionCondition.anyMatch("Ожидание отображения пункта меню", WebElement::isDisplayed))
                .filter(Condition.visible).first().shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Dialog dlgActions = new Dialog(action);
        if (params.isCheckPreBilling())
            preBillingCostAction = getPreBillingCostAction(preBillingPriceAction);
        dlgActions.getDialog().$x("descendant::button[.='Подтвердить']")
                .shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (params.isCheckAlert())
            new Alert().checkText(action).checkColor(Alert.Color.GREEN).close();
        Waiting.sleep(2000);
        if(Objects.nonNull(params.getNode())){
            $x("//a[.='{}']", productNameText).scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        }
        if (params.isWaitChangeStatus())
            waitChangeStatus();
        if (params.isCheckLastAction())
            checkLastAction(action);
        btnGeneralInfo.shouldBe(Condition.enabled).click();
    }

    @SneakyThrows
    @Step("Запуск действия '{action}' с параметрами и последующим нажатием на кнопку {textButton}")
    protected void runActionWithParameters(SelenideElement button, String action, String textButton, Executable executable, ActionParameters params) {
        String productNameText = null;
        //btnGeneralInfo.scrollIntoView(scrollCenter).shouldBe(Condition.enabled).click();
        if(Objects.nonNull(params.getNode())){
            productNameText = productName.getText();
            params.getNode().scrollIntoView(scrollCenter).click();
        }
        button.shouldBe(activeCnd).scrollIntoView(scrollCenter).hover().shouldBe(clickableCnd).click();
        $$x("//li[.='{}']", action).shouldBe(CollectionCondition.anyMatch("Ожидание отображения пункта меню", WebElement::isDisplayed))
                .filter(Condition.visible).first().shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        executable.execute();
        if (params.isCheckPreBilling())
            preBillingCostAction = getPreBillingCostAction(preBillingPriceAction);
        SelenideElement runButton = $x("//div[@role='dialog']//button[.='{}']", textButton);
        runButton.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (params.isCheckAlert())
            new Alert().checkText(action).checkColor(Alert.Color.GREEN).close();
        Waiting.sleep(3000);
        if(Objects.nonNull(params.getNode())){
            $x("//a[.='{}']", productNameText).scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        }
        if (params.isWaitChangeStatus())
            waitChangeStatus();
        if (params.isCheckLastAction())
            checkLastAction(action);
    }

    protected void runActionWithParameters(SelenideElement button, String action, String textButton, Executable executable) {
        runActionWithParameters(button, action, textButton, executable, ActionParameters.builder().build());
    }

    protected void runActionWithoutParameters(SelenideElement button, String action) {
        runActionWithoutParameters(button, action, ActionParameters.builder().build());
    }

    protected void runActionWithoutParameters(String headerBlock, String action, ActionParameters params) {
        runActionWithoutParameters(getBtnAction(headerBlock), action, params);
    }

    protected void runActionWithoutParameters(String headerBlock, String action) {
        runActionWithoutParameters(getBtnAction(headerBlock), action, ActionParameters.builder().build());
    }

    public void runActionWithParameters(String headerBlock, String action, String textButton, Executable executable, ActionParameters params) {
        runActionWithParameters(getBtnAction(headerBlock), action, textButton, executable, params);
    }

    public void runActionWithParameters(String headerBlock, String action, String textButton, Executable executable) {
        runActionWithParameters(getBtnAction(headerBlock), action, textButton, executable, ActionParameters.builder().build());
    }

    //new Table("Роли узла").getRowByIndex(0)
    @Step("Расширить диск {name} на {size}ГБ")
    public void expandDisk(String name, String size, SelenideElement node) {
        runActionWithParameters($x("//td[.='{}']/../descendant::button", name),
                "Расширить", "Подтвердить", () -> Input.byLabel("Дополнительный объем дискового пространства, Гб").setValue(size),
                ActionParameters.builder().node(node).build());
    }

    @Step("Проверка статуса заказа")
    public void checkErrorByStatus(ProductStatus status) {
        if (status.equals(ProductStatus.ERROR)) {
            Assertions.fail(String.format("Ошибка выполнения action продукта: %s. \nИтоговый статус: %s . \nОшибка: %s",
                    product, status, StateServiceSteps.getErrorFromStateService(product.getOrderId())));
        }
        else if(status.equals(ProductStatus.BLOCKED)) {
            Assertions.fail("Продукт в статусе заблокирован");
        }
        else log.info("Статус действия {}", status);
    }

    @Step("Проверка на содержание необходимых столбцов на вкладке История действий")
    public void checkHeadersHistory() {
        Assertions.assertEquals(Arrays.asList("Наименование", "Инициатор", "Дата создания", "Дата запуска", "Продолжительность, сек",
                "Статус", "Просмотр"), new History().getHeaders());
    }

    private static class TopInfo extends Table {
        public TopInfo() {
            super("Защита от удаления");
        }
    }

    public class History extends Table {
        public History() {
            super("Дата запуска");
        }

        @Override
        protected void open() {
            btnHistory.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        }

        public String lastActionName() {
            return getValueByColumnInFirstRow("Наименование").getText();
        }

        public ProductStatus lastActionStatus() {
            return getValueByColumnInFirstRow("Статус").$$x("descendant::*[name()='svg']")
                    .shouldBe(CollectionCondition.allMatch("Ожидание отображение статусов", WebElement::isDisplayed))
                    .stream()
                    .map(ProductStatus::new)
                    .filter(ProductStatus::isStatus)
                    .findFirst()
                    .orElseThrow(NotFoundException::new);
        }
    }

    @SneakyThrows
    @Step("Запуска действия с проверкой стоимости")
    public void runActionWithCheckCost(CompareType type, Executable executable) {
        TypifiedElement.refresh();
        waitChangeStatus();
        double currentCost = getCostOrder();
        executable.execute();
        if(preBillingCostAction == null)
            return;
        TypifiedElement.refresh();
        currentPriceOrder.shouldBe(Condition.matchText(String.valueOf(preBillingCostAction).replace('.', ',')), Duration.ofMinutes(3));
        Assertions.assertEquals(preBillingCostAction, getCostOrder(), "Стоимость предбиллинга экшена не равна стоимости после выполнения действия");
        if (type == CompareType.MORE)
            Assertions.assertTrue(preBillingCostAction > currentCost, String.format("%f <= %f", preBillingCostAction, currentCost));
        else if (type == CompareType.LESS)
            Assertions.assertTrue(preBillingCostAction < currentCost, String.format("%f >= %f", preBillingCostAction, currentCost));
        else if (type == CompareType.EQUALS)
            Assertions.assertEquals(preBillingCostAction, currentCost, 0.01d);
        else if (type == CompareType.ZERO) {
            Assertions.assertEquals(0.0d, preBillingCostAction, 0.001d);
            Assertions.assertEquals(0.0d, currentCost, 0.001d);
        }
    }

    protected abstract class VirtualMachine extends Table {
        public static final String POWER_STATUS_DELETED = "Удалено";
        public static final String POWER_STATUS_ON = "Включено";
        public static final String POWER_STATUS_OFF = "Выключено";

        abstract String getPowerStatus();

        @Override
        protected void open() {
            btnGeneralInfo.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        }

        public VirtualMachine(String columnName) {
            super(columnName);
        }

        public String getPowerStatus(String header) {
            return new ProductStatus(getValueByColumnInFirstRow(header).$x("descendant::*[name()='svg']").scrollIntoView(scrollCenter)).getStatus();
        }

        public void checkPowerStatus(String status) {
            Assertions.assertEquals(status, getPowerStatus(), "Статус питания не соотвествует ожидаемому");
        }
    }

    @Step("Проверка выполнения действия {action}")
    public void checkLastAction(String action) {
        btnHistory.shouldBe(Condition.enabled).click();
        History history = new History();
        checkErrorByStatus(history.lastActionStatus());
        Assertions.assertEquals(history.lastActionName(), action, "Название последнего действия не соответствует ожидаемому");
    }

    public History getHistoryTable() {
        return new History();
    }

    @Step("Получение стоимости заказа")
    public double getCostOrder() {
        currentPriceOrder.shouldBe(Condition.visible, Duration.ofMinutes(3));
        double cost = Double.parseDouble(Objects.requireNonNull(StringUtils.findByRegex("([-]?\\d{1,5},\\d{2})", currentPriceOrder.getText()))
                .replace(',', '.'));
        log.debug("Стоимость заказа {}", cost);
        return cost;
    }

    @Step("Получение стоимости предбиллинга")
    public static double getPreBillingCostAction(SelenideElement element) {
        element.shouldBe(Condition.visible);
        double cost = Double.parseDouble(Objects.requireNonNull(StringUtils.findByRegex("([-]?\\d{1,5},\\d{2})", element.getText()))
                .replace(',', '.'));
        log.debug("Стоимость предбиллинга {}", cost);
        return cost;
    }
}
