package ui.cloud.pages.orders;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import core.exception.CreateEntityException;
import core.helper.Configure;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import models.cloud.orderService.interfaces.IProduct;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.function.Executable;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebElement;
import steps.stateService.StateServiceSteps;
import ui.cloud.pages.CompareType;
import ui.cloud.tests.ActionParameters;
import ui.elements.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static com.codeborne.selenide.Selenide.$x;
import static core.helper.StringUtils.$$x;
import static core.helper.StringUtils.$x;
import static ui.elements.Alert.checkNoRedAlerts;
import static ui.elements.TypifiedElement.scrollCenter;

@Log4j2
@Getter
public abstract class IProductPage {

    private static final String HEADER_GROUP = "Группы";
    protected final SelenideElement prebillingCostElement = $x(
            "//div[contains(.,'Новая стоимость услуги')]/descendant::p[" +
                    "   (contains(.,'₽/сут.') and contains(.,','))" +
                    "   or text()='без изменений'" +
                    "   or .='Стоимость будет рассчитана  после создания заказа'" +
                    "   or .='будет рассчитана после выполнения действия']"
    );
    public final SelenideElement currentOrderCost = $x("(//p[contains(.,'₽/сут.') and contains(.,',')])[1]");
    protected Double prebillingCostValue;
    protected Button btnGeneralInfo = Button.byElement($x("//button[.='Общая информация']"));
    protected Tab generalInfoTab = Tab.byText("Общая информация");
    protected Tab historyTab = Tab.byText("История действий");
    IProduct product;
    protected final SelenideElement productName = $x("(//div[@type='large']/descendant::span)[1]");
    protected final SelenideElement mainItemPage = $x("(//a[contains(@class, 'Breadcrumb')])[2]");
    protected final Tab monitoringOsTab = Tab.byText("Мониторинг ОС");
    protected final SelenideElement generatePassButton = $x("//button[@aria-label='generate']");
    protected final SelenideElement noData = $x("//*[text() = 'Нет данных для отображения']");

    public IProductPage(IProduct product) {
        if (Objects.nonNull(product.getError()))
            throw new CreateEntityException(String.format("Продукт необходимый для выполнения теста был создан с ошибкой:\n%s", product.getError()));
        if (Objects.nonNull(product.getLink()))
            TypifiedElement.openPage(product.getLink());
        btnGeneralInfo.getButton().shouldBe(Condition.enabled);
        product.setLink(WebDriverRunner.getWebDriver().getCurrentUrl());
        product.addLinkProduct();
        this.product = product.buildFromLink();
    }

    //Для т1
    public IProductPage() {
    }

    public static SelenideElement getActionsMenuButton(String header) {
        return getActionsMenuButton(header, 1);
    }

    public static SelenideElement getActionsMenuButton(String header, int index) {
        return TypifiedElement.getNearElement("button[@id='actions-menu-button']", String.format("*[.='%s']", header), index);
    }

    @Step("Получение таблицы по заголовку")
    public static Table getTableByHeader(String header) {
        return new Table($$x("(//*[text() = '{}']/ancestor-or-self::*[count(.//table) = 1])[last()]//table", header).filter(Condition.visible).first());
    }

    public SelenideElement getHeaderBlock(String name) {
        return $x("//td[.='{}']/../descendant::button", name);
    }

    protected abstract void checkPowerStatus(String expectedStatus);

    public void waitChangeStatus() {
        mainItemPage.click();
        OrderUtils.waitChangeStatus(new TopInfo(), Duration.ofMinutes(8));
    }

    public void waitChangeStatus(Duration duration) {
        OrderUtils.waitChangeStatus(new TopInfo(), duration);
    }

    @Step("Переключение 'Защита от удаления' в состояние '{expectValue}'")
    public void switchProtectOrder(boolean checked) {
        String expectValue = "Защита от удаления выключена";
        if (checked)
            expectValue = "Защита от удаления включена";
        OrderStatus status = new OrderStatus(expectValue);
        runActionWithParameters(getLabel(), "Защита от удаления", "Подтвердить",
                () -> Input.byLabel("Включить защиту от удаления").click(), ActionParameters.builder().waitChangeStatus(false).checkPreBilling(false).checkLastAction(false).build());
        new TopInfo().getValueByColumnInFirstRow("Защита от удаления").$x("descendant::*[name()='svg']")
                .shouldBe(Condition.match(expectValue, e -> new OrderStatus(e).equals(status)), Duration.ofSeconds(10));
    }

    @Step("Получение label")
    public String getLabel() {
        return $x("//span[starts-with(text(),'AT-UI-')]").shouldBe(Condition.visible.because("Должно отображаться сообщение")).getText();
    }

    @Step("Проверка вкладки Мониторинг")
    public void checkMonitoringOs() {
        Assumptions.assumeTrue(Waiting.sleep(() -> monitoringOsTab.getElement().isDisplayed(), Duration.ofSeconds(5)),
                "Мониторинг недоступен");
        monitoringOsTab.getElement().scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        if (Configure.ENV.equals("ift")) {
            checkNoRedAlerts();
        } else new MonitoringOsPage(product).check();

    }

    @Step("Проверка вкладки Мониторинг кластера")
    public void checkClusterMonitoringOs() {
        String column = "Роли узла";
        int size = new Table(column).rowSize();
        for (int i = 0; i < size; i++) {
            SelenideElement element = new Table(column).getRowByIndex(i);
            element.shouldBe(Condition.visible.because("Должно отображаться сообщение")).scrollIntoView(scrollCenter).click();
            Assumptions.assumeTrue(Waiting.sleep(() -> monitoringOsTab.getElement().isDisplayed(), Duration.ofSeconds(5)),
                    "Мониторинг недоступен");
            monitoringOsTab.getElement().scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
            if (Configure.ENV.equals("ift")) {
                checkNoRedAlerts();
            } else {
                new MonitoringOsPage(product).check();
            }
            goToCluster();
        }
    }

    @Step("Запуск действия '{action}'")
    protected void runActionWithoutParameters(SelenideElement button, String action, ActionParameters params) {
        if (Objects.nonNull(params.getNode())) {
            params.getNode().scrollIntoView(scrollCenter).click();
        }
        if (params.isSimpleAction())
            Button.byElement(button).click();
        else
            Menu.byElement(button).select(action);
        Dialog dlgActions = Dialog.byTitle(action);
        if (params.isCheckPreBilling())
            prebillingCostValue = OrderUtils.getCostValue(prebillingCostElement);
        dlgActions.getDialog().$x("descendant::button[.='Подтвердить']")
                .shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (params.isCheckAlert())
            Alert.green(action);
        Waiting.sleep(2000);
        if (Objects.nonNull(params.getNode()))
            goToCluster();
        if (params.isWaitChangeStatus())
            waitChangeStatus();
        getOrderCost();
        if (params.isCheckLastAction()) {
            checkLastAction(action);
        }
    }

    @SneakyThrows
    @Step("Запуск действия '{action}' с параметрами и последующим нажатием на кнопку '{textButton}'")
    protected void runActionWithParameters(SelenideElement button, String action, String textButton, Executable executable, ActionParameters params) {
        String productNameText = null;
        if (Objects.nonNull(params.getNode())) {
            productNameText = productName.getText();
            params.getNode().scrollIntoView(scrollCenter).click();
        }
        if (params.isSimpleAction())
            Button.byElement(button).click();
        else
            Menu.byElement(button).select(action);
        executable.execute();
        if (params.isCheckPreBilling())
            prebillingCostValue = OrderUtils.getCostValue(prebillingCostElement);
        if (params.isClickCancel())
            textButton = "Отмена";
        SelenideElement runButton = $x("//div[@role='dialog']//button[.='{}']", textButton);
        runButton.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (params.isClickCancel())
            return;
        if (params.isCheckAlert())
            Alert.green(action);
        Waiting.sleep(3000);
        if (Objects.nonNull(params.getNode())) {
            $x("//a[.='{}']", productNameText).scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        }
        if (params.isWaitChangeStatus()) {
            if (params.getTimeout() == null) {
                waitChangeStatus();
            } else {
                waitChangeStatus(params.getTimeout());
            }
        }
        getOrderCost();
        if (params.isCheckLastAction()) {
            checkLastAction(action);
        }
    }

    public void goToCluster() {
        $x("//a[.='{}']", productName.getText()).scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
    }

    protected void runActionWithParameters(SelenideElement button, String action, String textButton, Executable executable) {
        runActionWithParameters(button, action, textButton, executable, ActionParameters.builder().build());
    }

    protected void runActionWithoutParameters(SelenideElement button, String action) {
        runActionWithoutParameters(button, action, ActionParameters.builder().build());
    }

    protected void runActionWithoutParameters(String headerBlock, String action, ActionParameters params) {
        runActionWithoutParameters(getActionsMenuButton(headerBlock), action, params);
    }

    protected void runActionWithoutParameters(String headerBlock, String action) {
        runActionWithoutParameters(getActionsMenuButton(headerBlock), action, ActionParameters.builder().build());
    }

    public void runActionWithParameters(String headerBlock, String action, String textButton, Executable executable, ActionParameters params) {
        runActionWithParameters(getActionsMenuButton(headerBlock), action, textButton, executable, params);
    }

    public void runActionWithParameters(String headerBlock, String action, String textButton, Executable executable) {
        runActionWithParameters(getActionsMenuButton(headerBlock), action, textButton, executable, ActionParameters.builder().build());
    }

    @Step("Расширить диск {name} на {size}ГБ")
    public void expandDisk(String name, String size, SelenideElement node) {
        runActionWithParameters($x("//td[.='{}']/../descendant::button", name),
                "Расширить", "Подтвердить", () -> Input.byLabel("Дополнительный объем дискового пространства, Гб").setValue(size),
                ActionParameters.builder().node(node).build());
    }

    @Step("Проверка статуса заказа")
    public void checkErrorByStatus(OrderStatus status) {
        if (status.equals(OrderStatus.ERROR)) {
            Assertions.fail(String.format("Ошибка выполнения action продукта: %s. \nИтоговый статус: %s . \nОшибка: %s",
                    product, status, StateServiceSteps.getErrorFromStateService(product.getProjectId(), product.getOrderId())));
        } else if (status.equals(OrderStatus.BLOCKED)) {
            Assertions.fail("Продукт в статусе заблокирован");
        } else log.info("Статус действия {}", status);
    }

    @Step("Проверка на содержание необходимых столбцов на вкладке История действий")
    public void checkHeadersHistory() {
        Assertions.assertEquals(Arrays.asList("Наименование", "Инициатор", "Дата создания", "Дата запуска", "Продолжительность",
                "Статус", "Просмотр"), new History().getHeaders());
    }


    @Step("Добавить новые группы {group} с ролью {role}")
    public void addGroup(String role, List<String> groups) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        runActionWithParameters("Роли", "Добавить группу доступа", "Подтвердить", () -> {
            Select.byLabel("Роль").set(role);
            groups.forEach(group -> Select.byLabel("Группы").set(group));
        });
        groups.forEach(group -> Assertions.assertTrue(new IProductPage.RoleTable().getGroupsRole(role).contains(group), "Не найдена группа " + group));
    }

    @Step("Изменить состав групп у роли {role} на {groups}")
    public void updateGroup(String role, List<String> groups) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        runActionWithParameters(new IProductPage.RoleTable().getRoleMenuElement(role), "Изменить состав группы", "Подтвердить", () -> {
            Select groupsElement = Select.byLabel("Группы").clear();
            groups.forEach(groupsElement::set);
        });
        groups.forEach(group -> Assertions.assertTrue(new IProductPage.RoleTable().getGroupsRole(role).contains(group), "Не найдена группа " + group));
    }

    public SelenideElement getVMElement() {
        return new Table("Роли узла").getRow(0).get();
    }

    @Step("Добавить новые группы {group} с ролью {role} в ноде")
    public void addGroupInNode(String role, List<String> groups) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        getVMElement().scrollIntoView(scrollCenter).click();
        runActionWithParameters("Роли", "Добавить группу доступа", "Подтвердить", () -> {
            Select.byLabel("Роль").setContains(role);
            groups.forEach(group -> Select.byLabel("Группы").set(group));
        });
        btnGeneralInfo.click();
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        getVMElement().scrollIntoView(scrollCenter).click();
        btnGeneralInfo.click(); // для задержки иначе не отрабатывает 305 строка
        groups.forEach(group -> Assertions.assertTrue(new IProductPage.RoleTable().getGroupsRole(role).contains(group), "Не найдена группа " + group));
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
    }

    @Step("Изменить состав групп у роли {role} на {groups} в ноде")
    public void updateGroupInNode(String role, List<String> groups) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        getVMElement().scrollIntoView(scrollCenter).click();
        runActionWithParameters(new IProductPage.RoleTable().getRoleMenuElement(role), "Изменить состав группы", "Подтвердить", () -> {
            Select groupsElement = Select.byLabel("Группы").clear();
            groups.forEach(groupsElement::set);
        }, ActionParameters.builder().node(getVMElement()).build());
        btnGeneralInfo.click();
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        getVMElement().scrollIntoView(scrollCenter).click();
        groups.forEach(group -> Assertions.assertTrue(new IProductPage.RoleTable().getGroupsRole(role).contains(group), "Не найдена группа " + group));
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
    }

    @Step("Удалить группу доступа с ролью {role}")
    public void deleteGroup(String role) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        runActionWithoutParameters(new IProductPage.RoleTable().getRoleMenuElement(role), "Удалить группу доступа");
        Assertions.assertFalse(getTableByHeader("Роли").isColumnValueContains("",
                role));
    }

    @Step("Удалить группу доступа с ролью {role}  в ноде")
    public void deleteGroupInNode(String role, String nameGroup) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        getVMElement().scrollIntoView(scrollCenter).click();
        runActionWithoutParameters(new IProductPage.RoleTable().getRoleMenuElement(role), "Удалить группу доступа");
        btnGeneralInfo.click();
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        getVMElement().scrollIntoView(scrollCenter).click();
        Assertions.assertFalse(getTableByHeader("Роли").isColumnValueContains("",
                role));
    }

    @SneakyThrows
    @Step("Запуск действия с проверкой стоимости")
    public void runActionWithCheckCost(CompareType type, Executable executable) {
        TypifiedElement.refreshPage();
        waitChangeStatus();
        double currentCost = getOrderCost();
        executable.execute();
        if (prebillingCostValue == null)
            return;
        TypifiedElement.refreshPage();
//        currentOrderCost.shouldBe(Condition.matchText(doubleToString(prebillingCostValue)), Duration.ofMinutes(10));
        Waiting.find(() -> prebillingCostValue.equals(getOrderCost()), Duration.ofMinutes(5),
                "Стоимость предбиллинга экшена не равна стоимости после выполнения действия");
        if (currentCost == prebillingCostValue && prebillingCostValue == 0)
            return;
        if (type == CompareType.MORE)
            Assertions.assertTrue(prebillingCostValue > currentCost, String.format("%f <= %f", prebillingCostValue, currentCost));
        else if (type == CompareType.LESS)
            Assertions.assertTrue(prebillingCostValue < currentCost, String.format("%f >= %f", prebillingCostValue, currentCost));
        else if (type == CompareType.EQUALS)
            Assertions.assertEquals(prebillingCostValue, currentCost, 0.01d);
        else if (type == CompareType.ZERO) {
            Assertions.assertEquals(0.0d, prebillingCostValue, 0.001d);
            Assertions.assertEquals(0.0d, getOrderCost(), 0.001d);
        }
    }

    @Step("Проверка выполнения действия {action}")
    public void checkLastAction(String action) {
        History history = new History();
        checkErrorByStatus(history.lastActionStatus());
        Assertions.assertEquals(history.lastActionName(), action, "Название последнего действия не соответствует ожидаемому");
    }

    public History getHistoryTable() {
        return new History();
    }

    @Step("Получение стоимости заказа")
    public double getOrderCost() {
        double cost = OrderUtils.getCostValue(currentOrderCost.shouldBe(Condition.visible.because("Должно отображаться сообщение"), Duration.ofMinutes(5)));
        log.debug("Стоимость заказа {}", cost);
        return cost;
    }

    static class TopInfo extends Table {
        public TopInfo() {
            super("Защита от удаления");
        }

        public String getOrderStatus() {
            return new OrderStatus(getValueByColumnInFirstRow("Статус").$x("descendant::*[name()='svg']")
                    .scrollIntoView(scrollCenter)).getStatus();
        }

        public void checkOrderStatus(String status) {
            Assertions.assertEquals(status, getOrderStatus(), "Статус заказа не соотвествует ожидаемому");
        }
    }

    //Таблица ролей
    public class RoleTable extends Table {
        public RoleTable() {
            super("Группы");
        }

        @Override
        protected void open() {
            btnGeneralInfo.click();
        }

        private SelenideElement getRoleMenuElement(String name) {
            return getRoleRow(name).$("button");
        }

        private SelenideElement getRoleRow(String name) {
            return getRowElementByColumnValue("", name);
        }

        private String getGroupsRole(String name) {
            return getRowByColumnValue("", name).getValueByColumn("Группы");
        }
    }

    public class History extends Table {
        public History() {
            super("Дата запуска");
        }

        @Override
        protected void open() {
            historyTab.switchTo();
        }

        public String lastActionName() {
            return getValueByColumnInFirstRow("Наименование").getText();
        }

        public OrderStatus lastActionStatus() {
            return getValueByColumnInFirstRow("Статус").$$x("descendant::*[name()='svg']")
                    .shouldBe(CollectionCondition.allMatch("Ожидание отображение статусов", WebElement::isDisplayed))
                    .stream()
                    .map(OrderStatus::new)
                    .filter(OrderStatus::isStatus)
                    .findFirst()
                    .orElseThrow(NotFoundException::new);
        }
    }

    protected abstract class VirtualMachine extends Table {
        public static final String POWER_STATUS_DELETED = "Удалено";
        public static final String POWER_STATUS_ON = "Включено";
        public static final String POWER_STATUS_OFF = "Выключено";

        public VirtualMachine(String columnName) {
            super(columnName);
        }

        protected abstract String getPowerStatus();

        @Override
        protected void open() {
            generalInfoTab.switchTo();
        }

        public String getPowerStatus(String header) {
            return new OrderStatus(getValueByColumnInFirstRow(header).$x("descendant::*[name()='svg']").scrollIntoView(scrollCenter)).getStatus();
        }

        public void checkPowerStatus(String status) {
            Assertions.assertEquals(status, getPowerStatus(), "Статус питания не соотвествует ожидаемому");
        }
    }
}
