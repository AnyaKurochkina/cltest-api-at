package ui.cloud.tests.productCatalog.orderTemplate;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.productCatalog.visualTeamplate.ItemVisualTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import steps.productCatalog.VisualTemplateSteps;
import ui.cloud.pages.ControlPanelIndexPage;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static steps.productCatalog.TagSteps.createTag;
import static steps.productCatalog.TagSteps.deleteTagByName;
import static ui.cloud.pages.productCatalog.orderTemplate.OrderTemplatesListPage.ORDER_TEMPLATE_NAME_COLUMN;

@Feature("Действия со списком шаблонов отображения")
public class OrderTemplatesListTests extends OrderTemplateBaseTest {

    @Test
    @TmsLink("646721")
    @DisplayName("Проверка заголовков списка, сортировка, пагинация")
    public void checkHeadersAndSorting() {
        new ControlPanelIndexPage().goToOrderTemplatesPage()
                .checkHeaders()
                .checkSortingByTitle()
                .checkSortingByName()
                .checkSortingByCreateDate()
                .checkSortingByState()
                .checkPagination();
    }

    @Test
    @TmsLink("1206221")
    @DisplayName("Поиск в списке шаблонов")
    public void searchOrderTemplateTest() {
        new ControlPanelIndexPage().goToOrderTemplatesPage()
                .findTemplateByValue(NAME, orderTemplate)
                .findTemplateByValue(TITLE, orderTemplate)
                .findTemplateByValue(NAME.substring(1).toUpperCase(), orderTemplate)
                .findTemplateByValue(TITLE.substring(1).toUpperCase(), orderTemplate);
    }

    @Test
    @TmsLink("770483")
    @DisplayName("Фильтрация списка шаблонов")
    public void filterOrderTemplatesTest() {
        new ControlPanelIndexPage().goToOrderTemplatesPage()
                .setTypeFilter("vm")
                .setProviderFilter("vsphere")
                .setStateFilter("Выключено")
                .applyFilters()
                .checkTemplateIsDisplayed(orderTemplate)
                .removeFilterTag("vsphere")
                .checkTemplateIsDisplayed(orderTemplate)
                .setStateFilter("Включено")
                .applyFilters()
                .checkTemplateIsNotDisplayed(orderTemplate)
                .clearFilters()
                .checkTemplateIsDisplayed(orderTemplate);
    }

    @Test
    @TmsLinks({@TmsLink("SOUL-1975"), @TmsLink("SOUL-1976")})
    @DisplayName("Добавить и удалить тег из списка шаблонов отображения")
    public void addAndDeleteTagFromList() {
        String tag1 = "qa_at_" + randomAlphanumeric(6).toLowerCase();
        createTag(tag1);
        ItemVisualTemplate orderTemplate2 = VisualTemplateSteps.createVisualTemplate(orderTemplate.getName() + "_2");
        new ControlPanelIndexPage()
                .goToOrderTemplatesPage()
                .search(orderTemplate.getName())
                .switchToGroupOperations()
                .selectAllRows()
                .editTags()
                .addTag(tag1)
                .closeDialog()
                .checkTags(ORDER_TEMPLATE_NAME_COLUMN, orderTemplate.getName(), tag1.substring(0, 7))
                .checkTags(ORDER_TEMPLATE_NAME_COLUMN, orderTemplate2.getName(), tag1.substring(0, 7))
                .editTags()
                .removeTag(tag1)
                .closeDialog()
                .checkTags(ORDER_TEMPLATE_NAME_COLUMN, orderTemplate.getName(), "")
                .checkTags(ORDER_TEMPLATE_NAME_COLUMN, orderTemplate2.getName(), "");
        deleteTagByName(tag1);
    }
}
