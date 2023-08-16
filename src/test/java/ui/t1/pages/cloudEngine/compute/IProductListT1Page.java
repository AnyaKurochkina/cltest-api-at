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

public class IProductListT1Page extends IProductT1Page<IProductListT1Page> {
    private String productLink;

    @Override
    protected void runActionWithoutParameters(SelenideElement button, String action, ActionParameters params) {
        synchronized (IProductListT1Page.class) {
            super.runActionWithoutParameters(button, action, params);
        }
    }

    @Override
    protected void runActionWithParameters(SelenideElement button, String action, String textButton, Executable executable, ActionParameters params) {
        synchronized (IProductListT1Page.class) {
            super.runActionWithParameters(button, action, textButton, executable, params);
        }
    }

    @Override
    public void waitChangeStatus() {
        productLink = WebDriverRunner.getWebDriver().getCurrentUrl();
        new IndexPage().goToHistory();
        OrderUtils.waitCreate(() ->
                Waiting.findWithRefresh(() -> !ComputeHistory.getLastActionStatus().getText().equals("В процессе"), Duration.ofMinutes(1)));
    }

    @Override
    public void checkLastAction(String action) {
        Assertions.assertEquals(action, new ComputeHistory().getFirstValueByColumn(Column.OPERATION));
        Assertions.assertEquals("Завершено", new ComputeHistory().getFirstValueByColumn(Column.STATUS));
        TypifiedElement.open(productLink);
    }
}
