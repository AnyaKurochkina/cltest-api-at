package ui.t1.pages.cloudEngine.compute;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebDriverRunner;
import core.utils.Waiting;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebElement;
import ui.elements.TypifiedElement;
import ui.t1.pages.IndexPage;

import java.time.Duration;

import static ui.t1.pages.cloudEngine.compute.ComputeHistory.COLUMN_OPERATION;
import static ui.t1.pages.cloudEngine.compute.ComputeHistory.COLUMN_STATUS;

public class IProductListT1Page extends IProductT1Page<IProductListT1Page> {
    private String productLink;


    @Override
    public void waitChangeStatus(){
        productLink = WebDriverRunner.getWebDriver().getCurrentUrl();
        new IndexPage().goToHistory();
        ComputeHistory.getLastActionStatus().shouldBe(new FinalStatus(), Duration.ofMinutes(1));
    }

    @Override
    public void checkLastAction(String action){
        Assertions.assertEquals(action, new ComputeHistory().getFirstValueByColumn(COLUMN_OPERATION));
        Assertions.assertEquals("Завершено", new ComputeHistory().getFirstValueByColumn(COLUMN_STATUS));
        TypifiedElement.open(productLink);
    }

    private static class FinalStatus extends Condition {
        public FinalStatus() {
            super("Ожидание финального статуса");
        }

        @Override
        public boolean apply(@NotNull Driver driver, @NotNull WebElement e) {
            if(!e.getText().equals("В процессе"))
                return true;
            Waiting.sleep(11000);
            TypifiedElement.refresh();
            Waiting.sleep(4000);
            return false;
        }
    }
}
