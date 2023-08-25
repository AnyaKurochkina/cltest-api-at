package ui.cloud.tests.productCatalog.tag;

import core.helper.Configure;
import core.helper.DataFileHelper;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.tag.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.tag.TagsListPage;

import static steps.productCatalog.TagSteps.*;

@Feature("Импорт тега из файла")
public class ImportTagTest extends TagTest {

    @Test
    @DisplayName("Импортировать тег")
    @TmsLink("SOUL-1066")
    public void importTag() {
        Tag tag = createTag("import_tag_test_api");
        String name = tag.getName();
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/tag/importTag.json";
        DataFileHelper.write(filePath, exportTagByName(name).toString());
        deleteTagByName(name);
        new ControlPanelIndexPage().goToTagsPage().importObject(filePath);
        new TagsListPage().findTagByValue(name, tag);
        deleteTagByName(name);
    }
}
