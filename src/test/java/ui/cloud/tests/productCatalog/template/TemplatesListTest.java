package ui.cloud.tests.productCatalog.template;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.template.TemplatesListPage;

@Feature("Просмотр списка шаблонов узлов")
public class TemplatesListTest extends TemplateBaseTest {

    @Test
    @TmsLink("486731")
    @DisplayName("Проверка заголовков списка, сортировка")
    public void checkHeadersAndSorting() {
        new IndexPage().goToTemplatesPage()
                .checkHeaders()
                .checkSortingByTitle()
                .checkSortingByName()
                .checkSortingByCreateDate();
    }

    @Test
    @TmsLink("1116098")
    @DisplayName("Поиск в списке шаблонов")
    public void searchTemplateTest() {
        new IndexPage().goToTemplatesPage()
                .findTemplateByValue(NAME, template)
                .findTemplateByValue(TITLE, template)
                .findTemplateByValue(NAME.substring(1).toUpperCase(), template)
                .findTemplateByValue(TITLE.substring(1).toUpperCase(), template);
    }

    @Test
    @TmsLink("1116686")
    @DisplayName("Подсветка ранее открытого шаблона узлов")
    public void returnFromTemplatePageTest() {
        new IndexPage().goToTemplatesPage()
                .findAndOpenTemplatePage(NAME)
                .goToTemplatesList()
                .checkTemplateIsHighlighted(NAME);
        new TemplatesListPage().openTemplatePage(NAME);
        Selenide.back();
        new TemplatesListPage().checkTemplateIsHighlighted(NAME);
        new TemplatesListPage().openTemplatePage(NAME)
                .backToTemplatesList()
                .checkTemplateIsHighlighted(NAME);
    }
}
