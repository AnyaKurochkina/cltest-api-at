package ui.cloud.tests.productCatalog.tag;

import core.utils.AssertUtils;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.tag.Tag;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.EntityListPage;
import ui.cloud.pages.productCatalog.tag.TagsListPage;
import ui.elements.Table;

import static steps.productCatalog.TagSteps.createTag;

@Feature("Список тегов")
public class TagsListTest extends TagTest {

    @Test
    @TmsLink("SOUL-1058")
    @DisplayName("Проверка заголовков списка тегов, сортировка")
    public void checkHeadersAndSorting() {
        new ControlPanelIndexPage().goToTagsPage();
        AssertUtils.assertHeaders(new Table("Наименование"), "Наименование", "Дата создания", "", "");
        EntityListPage.checkSortingByStringField("Наименование");
        EntityListPage.checkSortingByDateField("Дата создания");
    }

    @Test
    @TmsLink("SOUL-1059")
    @DisplayName("Поиск тега")
    public void search() {
        String name = "qa_at_tag_" + RandomStringUtils.randomAlphanumeric(8).toLowerCase();
        Tag tag = createTag(name);
        tagList.add(name);
        TagsListPage page = new ControlPanelIndexPage().goToTagsPage();
        page.findTagByValue(name, tag);
        page.findTagByValue(name.substring(1).toUpperCase(), tag);
    }
}
