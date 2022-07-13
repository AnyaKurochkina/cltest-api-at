package ui.cloud.pages.services;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.EntitiesUtils;
import ui.cloud.pages.ProductStatus;
import ui.elements.Alert;
import ui.elements.Table;

import java.time.Duration;

import static core.helper.StringUtils.$x;
import static tests.Tests.activeCnd;

@Getter
public class IServicePage {
    SelenideElement btnRun = $x("//button[.='Запуск']");
    SelenideElement btnRuns = $x("//button[.='Запуски']");
    SelenideElement btnParams = $x("//button[.='Параметры']");

    String serviceName;

    public void run() {
        btnRun.shouldBe(activeCnd).click();
        new Alert().checkText("Успешно создано действие " + serviceName).checkColor(Alert.Color.GREEN);
        EntitiesUtils.waitChangeStatus(new Runs(), Duration.ofSeconds(30));
        Assertions.assertEquals(ProductStatus.SUCCESS, new Runs().getStatus());
        new Runs().getValueByColumnInFirstRow("Просмотр").$x("descendant::button[last()]").shouldBe(Condition.enabled).click();
        new EntitiesUtils().checkGraphScheme();
    }

    private class Runs extends Table {
        @Override
        protected void open(){
            btnRuns.shouldBe(Condition.enabled).click();
        }

        public Runs() {
            super("Дата запуска");
        }

        public String getStatus(){
            return getValueByColumnInFirstRow("Статус").scrollIntoView(true).$x("descendant::*[@title]").getAttribute("title");
        }
    }

    private class TopInfo extends Table {
        @Override
        protected void open(){
            btnParams.shouldBe(Condition.enabled).click();
        }

        public TopInfo() {
            super("Расписание");
        }
    }

}
