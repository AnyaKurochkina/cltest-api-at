package ui.cloud.tests.productCatalog.template;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.template.TemplatesListPage;
import ui.uiModels.Template;

@Epic("Шаблоны узлов")
@Feature("Просмотр списка шаблонов узлов")
public class TemplatesListTest extends TemplateBaseTest {

    @Test
    @TmsLink("486731")
    @DisplayName("Проверка заголовков списка, сортировка")
    public void checkGraphsListSorting() {
        new IndexPage().goToTemplatesPage()
                .checkTemplatesListHeaders()
                .checkSortingByTitle()
                .checkSortingByName()
                .checkSortingByCreateDate();
    }

    @TmsLink("1116098")
    @Test
    @DisplayName("Поиск в списке шаблонов")
    public void searchGraphTest() {
        Template template = new Template(NAME);
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
                .sortByCreateDate()
                .lastPage()
                .openTemplatePage(NAME)
                .goToTemplatesList()
                .checkTemplateIsHighlighted(NAME);
        new TemplatesListPage().openTemplatePage(NAME);
        Selenide.back();
        new TemplatesListPage().checkTemplateIsHighlighted(NAME);
    }
}
