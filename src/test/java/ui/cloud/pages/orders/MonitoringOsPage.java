package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.orderService.products.Windows;
import org.junit.jupiter.api.Assertions;
import ui.elements.Select;
import ui.elements.TypifiedElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.actions;
import static core.helper.StringUtils.$x;

public class MonitoringOsPage {
    public static final String FIRST_FILTER = "Последние 14 дней";
    public static final String SECOND_FILTER = "Последний час";
    final List<String> graphNames = new ArrayList<String>() {
        {
            add("CPU Util (%)");
            add("Memory Util (%)");
            add("SWAP Util (%)");
            add("Space Util (%)");
            add("Disk Read Data (MB)");
            add("Disk Write Data (MB)");
            add("Network Interface Received (MB)");
            add("Network Interface Sent (MB)");
        }
    };
    private final SelenideElement noData = $x("//*[.='Нет данных для графика']");
    private final SelenideElement btnResetFilter = $x("//button[.='Сбросить фильтры']");
    private final Select period = Select.byLabel("Период");
    private final SelenideElement btnApply = $x("//button[.='Применить']");

    public MonitoringOsPage(IProduct product) {
        if (product instanceof Windows)
            graphNames.remove("SWAP Util (%)");
        btnResetFilter.shouldBe(Condition.visible.because("Должно отображаться сообщение"));
        period.getElement().shouldBe(Condition.visible.because("Должно отображаться сообщение"));
        btnApply.shouldBe(Condition.visible.because("Должно отображаться сообщение"));
    }

    @Step("Проверка вкладки мониторинга")
    public void check() {
        Assertions.assertFalse(noData.isDisplayed(), "Данные для графиков не были загружены");
        List<Graph> graphs = graphNames.stream().map(Graph::new).collect(Collectors.toList());
        switchFilter();
        Assertions.assertTrue(graphs.stream().filter(Graph::isEqualsState).count() < graphs.size() / 2, "Информация на графиках не обновилась");
    }

    public void switchFilter() {
        String filter = FIRST_FILTER;
        if (filter.equals(period.getValue()))
            filter = SECOND_FILTER;
        period.set(filter);
        btnApply.click();
        $x("//span[.='{}']", filter.toLowerCase()).shouldBe(Condition.visible.because("Должно отображаться сообщение"));
    }

    @Getter
    public static class Graph {
        SelenideElement graph;
        String name;
        String state;

        public Graph(String name) {
            this.name = name;
            this.graph = $x("(//div[.='{}']/following-sibling::*//*[name()='svg'])[1]", name).shouldBe(Condition.visible.because("Должно отображаться сообщение"));
            updateState();
        }

        public boolean isEqualsState() {
            String previousState = state;
            updateState();
            return state.equals(previousState);
        }

        @Step("Обновление данных по графику")
        public void updateState() {
            graph.scrollIntoView(TypifiedElement.scrollCenter);
            actions().moveToElement(graph.getWrappedElement(), -(graph.getRect().getWidth() / 4), 0).perform();
            state = $x("//ul[@class='recharts-tooltip-item-list']").shouldBe(Condition.visible.because("Должно отображаться сообщение")).getText();
        }
    }
}
