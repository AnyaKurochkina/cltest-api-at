package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Assertions;
import ui.elements.DropDown;

import java.util.Arrays;
import java.util.List;

import static core.helper.StringUtils.$x;

public class MonitoringOsPage {
    private final SelenideElement noData = $x("//*[.='Нет данных для графика']");
    SelenideElement btnResetFilter = $x("//button[.='Сбросить фильтры']");
    DropDown period = DropDown.byLabel("Период");
    SelenideElement btnApply = $x("//button[.='Применить']");
    final List<String> graphNames = Arrays.asList("CPU Util (%)", "Memory Util (%)", "SWAP Util (%)", "Space Util (%)", "Disk Read Data (MB)", "Disk Write Data (MB)",
            "Network Interface Received (MB)", "Network Interface Sent (MB)");

    public MonitoringOsPage() {
        btnResetFilter.shouldBe(Condition.visible);
        period.getElement().shouldBe(Condition.visible);
        btnApply.shouldBe(Condition.visible);
    }

    public void check(){
        for (String graphName : graphNames) {
            $x("//div[.='{}']", graphName).shouldBe(Condition.visible);
        }
        Assertions.assertFalse(noData.isDisplayed(), "Данные для графиков не были загружены");
    }
}
