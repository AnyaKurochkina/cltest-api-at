package ui.cloud.tests.productCatalog.service.tags;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.service.ServicePage;
import ui.cloud.tests.productCatalog.service.ServiceBaseTest;

public class CreateTagsTest extends ServiceBaseTest {

    @Test
    @TmsLink("644729")
    @DisplayName("Создание тегов для фильтра")
    public void addTagsTest() {
        new IndexPage().goToServicesListPagePC()
                .findAndOpenServicePage(NAME);
        addTag();
        addExcludeTag();
    }

    private void addTag() {
        String tag = "sys_ip";
        String[] values = {"10.226.3.74", "10.226.3.75"};
        new ServicePage()
                .addTag(tag, values)
                .checkTagsTable(tag, values)
                .checkDataSourceContainsValue(values[0]);
    }

    private void addExcludeTag() {
        String tag = "sys_vm_os_type";
        String[] values = {"windows", "linux"};
        new ServicePage()
                .addExcludeTag(tag, values)
                .checkExcludeTagsTable(tag, values)
                .checkDataSourceContainsValue(values[1]);
    }
}
