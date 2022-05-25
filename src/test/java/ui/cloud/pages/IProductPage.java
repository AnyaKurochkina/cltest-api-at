package ui.cloud.pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import models.orderService.interfaces.IProduct;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import steps.stateService.StateServiceSteps;
import ui.elements.Dialog;
import ui.elements.Table;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.open;
import static core.helper.StringUtils.$x;

@Log4j2
public abstract class IProductPage {
    TopInfo topInfo;
    IProduct product;
    Condition activeCnd = Condition.and("visible and enabled", Condition.visible, Condition.enabled);
    Condition clickableCnd = Condition.not(Condition.cssValue("cursor", "default"));

    SelenideElement btnHistory = $x("//button[.='История действий']");
    SelenideElement btnGeneralInfo = $x("//button[.='Общая информация']");

    public IProductPage(IProduct product) {
        if (Objects.nonNull(product.getLink()))
            open(product.getLink());
        btnGeneralInfo.shouldBe(Condition.enabled);
        product.setLink(WebDriverRunner.getWebDriver().getCurrentUrl());
        this.product = product.buildFromLink();
        topInfo = new TopInfo();
    }

    @Step("Ожидание выполнение действия с продуктом")
    public void waitChangeStatus() {
        List<String> titles = topInfo.getValueByColumnInFirstRow("Статус").$$x("descendant::*[@title]")
                .shouldBe(CollectionCondition.noneMatch("Ожидание заверешения действия", e ->
                        ProductStatus.isNeedWaiting(e.getAttribute("title"))), Duration.ofMillis(20000 * 1000))
                .stream().map(e -> e.getAttribute("title")).collect(Collectors.toList());
        log.debug("Итоговый статус: {}", titles);
    }

    @Step("Проверка выполнения последнего действия")
    public void checkLastAction() {
        btnHistory.shouldBe(Condition.enabled).click();
        History history = new History();
        checkErrorByStatus(history.lastActionStatus());
    }

    private SelenideElement getBtnAction(String header) {
        return $x("//ancestor::div[.='{}Действия']//button[.='Действия']", header);
    }

    @Step("Запуск действия '{action}' в блоке '{headerBlock}'")
    public void runActionWithoutParameters(String headerBlock, String action) {
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        getBtnAction(headerBlock).shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        $x("//li[.='{}']", action).shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Dialog dlgActions = new Dialog(action);
        dlgActions.getDialog().$x("descendant::button[.='Подтвердить']")
                .shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        dlgActions.getDialog().shouldNotBe(Condition.visible);
        Waiting.sleep(3000);
    }

    @SneakyThrows
    @Step("Запуск действия '{action}' в блоке '{headerBlock}' с параметрами")
    public void runActionWithParameters(String headerBlock, String action, Executable executable) {
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        getBtnAction(headerBlock).shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        $x("//li[.='{}']", action).shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        executable.execute();
        Waiting.sleep(3000);
    }

    public void checkErrorByStatus(String status) {
        if (status.equals(ProductStatus.ERROR)) {
            Assertions.fail(String.format("Ошибка выполнения action продукта: %s. \nИтоговый статус: %s . \nОшибка: %s",
                    product, status, StateServiceSteps.GetErrorFromStateService(product.getOrderId())));
        }
    }

    private static class TopInfo extends Table {
        public TopInfo() {
            super("Защита от удаления");
        }
    }

    private static class History extends Table {
        History() {
            super("Дата запуска");
        }

        public String lastActionStatus() {
            return getValueByColumnInFirstRow("Статус").$x("descendant::*[@title]").getAttribute("title");
        }
    }

}
