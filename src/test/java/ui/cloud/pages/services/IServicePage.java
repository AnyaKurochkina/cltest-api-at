package ui.cloud.pages.services;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.orders.OrderUtils;
import ui.cloud.pages.orders.ProductStatus;
import ui.elements.Alert;
import ui.elements.Graph;
import ui.elements.Table;

import java.time.Duration;

import static core.helper.StringUtils.$x;
import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;

@Getter
public class IServicePage {
    SelenideElement btnRun = $x("//button[.='Запуск']");
    SelenideElement btnRuns = $x("//button[.='Запуски']");
    SelenideElement btnParams = $x("//button[.='Параметры']");

    String serviceName;

    public void run() {
        btnRun.shouldBe(activeCnd).click();
        Alert.green("Успешно создано действие " + serviceName);
        OrderUtils.waitChangeStatus(new Runs(), Duration.ofSeconds(30));
        Assertions.assertEquals(ProductStatus.SUCCESS, new Runs().getStatus());
    }

    public SelenideElement getMenuLastRunsElement() {
        return new Table("Дата запуска").getRowByIndex(0).$x("descendant::button[@id='actions-menu-button']");
    }

    public void runAction(SelenideElement button, String action) {
        button.shouldBe(activeCnd).scrollIntoView("{block: 'center'}").hover().shouldBe(clickableCnd).click();
        $x("//li[.='{}']", action).shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
    }

    public void checkGraph() {
        new Runs().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new Graph().throwNotRunNodes().throwSkipNodes().throwRunNodes().checkGraph();
    }

    private class Runs extends Table {
        @Override
        protected void open() {
            btnRuns.shouldBe(Condition.enabled).click();
        }

        public Runs() {
            super("Дата запуска");
        }

        public ProductStatus getStatus() {
            return new ProductStatus(getValueByColumnInFirstRow("Статус").scrollIntoView(true).$x("descendant::*[name()='svg']"));
        }
    }

    private class TopInfo extends Table {
        @Override
        protected void open() {
            btnParams.shouldBe(Condition.enabled).click();
        }

        public TopInfo() {
            super("Расписание");
        }
    }

}
