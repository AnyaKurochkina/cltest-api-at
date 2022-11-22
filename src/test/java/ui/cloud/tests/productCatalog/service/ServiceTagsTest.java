package ui.cloud.tests.productCatalog.service;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.service.ServicePage;

public class ServiceTagsTest extends ServiceBaseTest {
    private final String tagName = "sys_ip";
    private final String excludeTagName = "sys_vm_os_type";

    @Test
    @TmsLink("644729")
    @DisplayName("Добавление тегов для фильтра")
    public void addTagsTest() {
        new IndexPage().goToServicesListPagePC()
                .findAndOpenServicePage(NAME);
        addTag(tagName, new String[]{"10.226.3.74", "10.226.3.75"});
        addExcludeTag(excludeTagName, new String[]{"windows", "linux"});
    }

    @Test
    @TmsLink("643299")
    @DisplayName("Редактирование тегов для фильтра")
    public void editTagsTest() {
        new IndexPage().goToServicesListPagePC()
                .findAndOpenServicePage(NAME);
        addTag(tagName, new String[]{"10.226.3.74", "10.226.3.75"});
        editTag(tagName, new String[]{"10.226.3.76"});
        addExcludeTag(excludeTagName, new String[]{"windows", "linux"});
        editExcludeTag(excludeTagName, new String[]{"other"});
    }

    @Test
    @TmsLink("644929")
    @DisplayName("Удаление тегов для фильтра")
    public void deleteTagsTest() {
        new IndexPage().goToServicesListPagePC()
                .findAndOpenServicePage(NAME);
        addTag(tagName, new String[]{"10.226.3.74", "10.226.3.75"});
        deleteTag(tagName);
        addExcludeTag(excludeTagName, new String[]{"windows", "linux"});
        deleteExcludeTag(excludeTagName);
    }

    private void addTag(String tagName, String[] values) {
        new ServicePage()
                .addTag(tagName, values)
                .checkTagsTable(tagName, values)
                .checkDataSourceContainsValue(values[0])
                .saveWithPatchVersion();
    }

    private void addExcludeTag(String excludeTagName, String[] values) {
        new ServicePage()
                .addExcludeTag(excludeTagName, values)
                .checkExcludeTagsTable(excludeTagName, values)
                .checkDataSourceContainsValue(values[1])
                .saveWithPatchVersion();
    }

    private void editTag(String tagName, String[] values) {
        new ServicePage()
                .editTag(tagName, values)
                .checkTagsTable(tagName, values)
                .checkDataSourceContainsValue(values[0])
                .saveWithPatchVersion();
    }

    private void editExcludeTag(String tagName, String[] values) {
        new ServicePage()
                .editExcludeTag(tagName, values)
                .checkExcludeTagsTable(tagName, values)
                .checkDataSourceContainsValue(values[0])
                .saveWithPatchVersion();
    }

    private void deleteTag(String tagName) {
        new ServicePage()
                .deleteTag(tagName)
                .checkTagsTableIsEmpty()
                .checkDataSourceDoesNotContainValue(tagName)
                .saveWithPatchVersion();
    }

    private void deleteExcludeTag(String excludeTagName) {
        new ServicePage()
                .deleteExcludeTag(excludeTagName)
                .checkExcludeTagsTableIsEmpty()
                .checkDataSourceDoesNotContainValue(excludeTagName)
                .saveWithPatchVersion();
    }
}
