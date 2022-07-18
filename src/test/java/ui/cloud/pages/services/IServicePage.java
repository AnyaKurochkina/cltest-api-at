package ui.cloud.pages.services;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import core.helper.StringUtils;
import core.utils.Waiting;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import steps.day2.Day2Steps;
import ui.cloud.pages.EntitiesUtils;
import ui.cloud.pages.ProductStatus;
import ui.cloud.tests.ActionParameters;
import ui.elements.Alert;
import ui.elements.Table;

import java.time.Duration;

import static core.helper.StringUtils.$x;
import static tests.Tests.activeCnd;
import static tests.Tests.clickableCnd;

@Getter
public class IServicePage {
    SelenideElement btnRun = $x("//button[.='Запуск']");
    SelenideElement btnRuns = $x("//button[.='Запуски']");
    SelenideElement btnParams = $x("//button[.='Параметры']");

    String serviceName;

    public void run() {
        btnRun.shouldBe(activeCnd).click();
        new Alert().checkText("Успешно создано действие " + serviceName).checkColor(Alert.Color.GREEN).close();
        EntitiesUtils.waitChangeStatus(new Runs(), Duration.ofSeconds(30));
        Assertions.assertEquals(ProductStatus.SUCCESS, new Runs().getStatus());
    }

    public SelenideElement getMenuLastRunsElement() {
        return new Table("Дата запуска").getRowByIndex(0).$x("descendant::button[@id='actions-menu-button']");
    }

    public void runAction(SelenideElement button, String action) {
        button.shouldBe(activeCnd).scrollIntoView("{block: 'center'}").hover().shouldBe(clickableCnd).click();
        $x("//li[.='{}']", action).shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
    }

    public void checkGraph(String projectId) {
        new Runs().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        Waiting.sleep(1000);
        String operationCardId = StringUtils.findByRegex("/([\\w]{8}-[\\w]{4}-[\\w]{4}-[\\w]{4}-[\\w]{12})/", WebDriverRunner.getWebDriver().getCurrentUrl());
        new EntitiesUtils().checkGraphScheme(Day2Steps.getOperationsGraph(Day2Steps.getOperations(operationCardId, projectId), projectId));
    }

    private class Runs extends Table {
        @Override
        protected void open() {
            btnRuns.shouldBe(Condition.enabled).click();
        }

        public Runs() {
            super("Дата запуска");
        }

        public String getStatus() {
            return getValueByColumnInFirstRow("Статус").scrollIntoView(true).$x("descendant::*[@title]").getAttribute("title");
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
