package ui.t1.pages.cloudEngine.compute;

import com.codeborne.selenide.WebDriverRunner;
import core.utils.Waiting;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.orders.OrderUtils;
import ui.t1.pages.IProductT1Page;
import ui.t1.pages.cloudEngine.Column;
import ui.elements.TypifiedElement;
import ui.t1.pages.IndexPage;

import java.time.Duration;

public class IProductListT1Page extends IProductT1Page<IProductListT1Page> {
    private String productLink;


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
