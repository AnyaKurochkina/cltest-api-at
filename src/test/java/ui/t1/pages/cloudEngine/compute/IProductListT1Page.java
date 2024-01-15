package ui.t1.pages.cloudEngine.compute;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import core.utils.Waiting;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import ui.cloud.pages.orders.OrderUtils;
import ui.cloud.tests.ActionParameters;
import ui.elements.TypifiedElement;
import ui.t1.pages.IProductT1Page;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.Column;

import java.time.Duration;

import static ui.cloud.pages.orders.OrderUtils.getCurrentProjectId;
import static ui.t1.tests.engine.AbstractComputeTest.historyMutex;

public class IProductListT1Page extends IProductT1Page<IProductListT1Page> {
    private String productLink;

    @Override
    protected void runActionWithoutParameters(SelenideElement button, String action, ActionParameters params) {
        synchronized (historyMutex.get(getCurrentProjectId())) {
            super.runActionWithoutParameters(button, action, params);
        }
    }

    @Override
    protected void runActionWithParameters(SelenideElement button, String action, String textButton, Executable executable, ActionParameters params) {
        synchronized (historyMutex.get(getCurrentProjectId())) {
            super.runActionWithParameters(button, action, textButton, executable, params);
        }
    }

    @Override
    public void waitChangeStatus() {
        productLink = WebDriverRunner.getWebDriver().getCurrentUrl();
        new IndexPage().goToHistory();
        OrderUtils.waitCreate(() ->
                Waiting.findWithRefresh(() -> !ComputeHistory.getLastActionStatus().getText().equals("В процессе"), Duration.ofMinutes(1)));
        TypifiedElement.openPage(productLink);
    }

    @Override
    public void checkLastAction(String action) {
        new IndexPage().goToHistory();
        checkActionByIndex(0, action);
        TypifiedElement.openPage(productLink);
    }

    public void checkActionByIndex(int index, String action) {
        Assertions.assertEquals(action, new ComputeHistory().getRow(index).getValueByColumn(Column.OPERATION));
        Assertions.assertEquals("Завершено", new ComputeHistory().getRow(index).getValueByColumn(Column.STATUS));
    }
}
