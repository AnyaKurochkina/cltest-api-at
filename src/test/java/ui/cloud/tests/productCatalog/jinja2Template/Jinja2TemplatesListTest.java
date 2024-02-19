package ui.cloud.tests.productCatalog.jinja2Template;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.jinja2Template.Jinja2TemplatesListPage;
import ui.elements.Table;

import static core.utils.AssertUtils.assertHeaders;

@Feature("Список шаблонов Jinja2")
public class Jinja2TemplatesListTest extends Jinja2TemplateBaseTest {

    private final String nameColumn = "Код шаблона";

    @Test
    @TmsLink("767065")
    @DisplayName("Просмотр списка шаблонов Jinja2, сортировка, пагинация")
    public void viewJinja2TemplatesList() {
        new ControlPanelIndexPage()
                .goToJinja2TemplatesListPage();
        assertHeaders(new Table(nameColumn),
                "Наименование", nameColumn, "Дата создания", "Дата изменения", "Описание", "", "");
        Jinja2TemplatesListPage page = new Jinja2TemplatesListPage();
        page.checkSorting().checkPagination();
    }

    @Test
    @TmsLink("1601364")
    @DisplayName("Поиск в списке шаблонов Jinja2")
    public void searchJinja2Templates() {
        new ControlPanelIndexPage().goToJinja2TemplatesListPage()
                .findJinja2TemplateByValue(NAME, jinja2Template)
                .findJinja2TemplateByValue(TITLE, jinja2Template)
                .findJinja2TemplateByValue(NAME.substring(1).toUpperCase(), jinja2Template)
                .findJinja2TemplateByValue(TITLE.substring(1).toUpperCase(), jinja2Template);
    }
}
