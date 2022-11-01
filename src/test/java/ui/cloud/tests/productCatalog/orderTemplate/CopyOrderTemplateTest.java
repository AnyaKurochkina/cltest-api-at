package ui.cloud.tests.productCatalog.orderTemplate;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.orderTemplate.OrderTemplatePage;

@Feature("Копирование шаблона отображения")
public class CopyOrderTemplateTest extends OrderTemplateBaseTest {

    @Test
    @TmsLink("679022")
    @DisplayName("Копирование шаблона")
    public void copyTemplateTest() {
        new IndexPage().goToOrderTemplatesPage()
                .copyTemplate(NAME);
        orderTemplate.setName(NAME + "-clone");
        new OrderTemplatePage().checkAttributes(orderTemplate);
        deleteOrderTemplate(orderTemplate.getName());
    }
}
