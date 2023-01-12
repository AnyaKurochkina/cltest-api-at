package ui.t1.pages.cloudEngine.compute;

import com.codeborne.selenide.WebDriverRunner;
import core.utils.Waiting;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.EntitiesUtils;
import ui.elements.TypifiedElement;
import ui.t1.pages.IndexPage;

import java.time.Duration;

import static ui.t1.pages.cloudEngine.compute.ComputeHistory.COLUMN_OPERATION;
import static ui.t1.pages.cloudEngine.compute.ComputeHistory.COLUMN_STATUS;

public class IProductListT1Page extends IProductT1Page<IProductListT1Page> {
    private String productLink;


    @Override
    public void waitChangeStatus() {
        productLink = WebDriverRunner.getWebDriver().getCurrentUrl();
        new IndexPage().goToHistory();
        EntitiesUtils.waitCreate(() ->
                Waiting.findWidthRefresh(() -> !ComputeHistory.getLastActionStatus().getText().equals("В процессе"), Duration.ofMinutes(1)));
    }

    @Override
    public void checkLastAction(String action) {
        Assertions.assertEquals(action, new ComputeHistory().getFirstValueByColumn(COLUMN_OPERATION));
        Assertions.assertEquals("Завершено", new ComputeHistory().getFirstValueByColumn(COLUMN_STATUS));
        TypifiedElement.open(productLink);
    }
}
