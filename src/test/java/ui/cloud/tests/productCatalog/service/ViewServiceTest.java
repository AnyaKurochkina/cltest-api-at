package ui.cloud.tests.productCatalog.service;

import core.enums.Role;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.GlobalUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.DiffPage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ViewServiceTest extends ServiceBaseTest {

    @Test
    @TmsLink("1206039")
    @DisplayName("Сравнение версий сервиса")
    public void compareVersionsTest() {
        new IndexPage().goToServicesListPagePC()
                .findAndOpenServicePage(NAME)
                .setExtraData("{\"test_value\":1}")
                .saveWithPatchVersion()
                .goToVersionComparisonTab();
        new DiffPage()
                .checkCurrentVersionInDiff("1.0.1")
                .compareWithVersion("1.0.0")
                .selectVersion("1.0.0")
                .checkCurrentVersionInDiff("1.0.0")
                .compareWithVersion("1.0.0")
                .compareWithVersion("1.0.1");
    }

    @Test
    @TmsLink("854612")
    @DisplayName("Просмотр JSON сервиса")
    public void viewJSONTest() {
        new IndexPage().goToServicesListPagePC()
                .findAndOpenServicePage(NAME)
                .checkJSONcontains(service.getId());
    }

    @Test
    @TmsLink("853403")
    @DisplayName("Просмотр аудита по сервису")
    public void viewServiceAuditTest() {
        GlobalUser user = GlobalUser.builder().role(Role.PRODUCT_CATALOG_ADMIN).build().createObject();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");
        new IndexPage().goToServicesListPagePC()
                .findAndOpenServicePage(NAME)
                .goToAuditTab()
                .checkFirstRecord(LocalDateTime.now().format(formatter), user.getUsername(), "create", "services", "201", "создан");
    }
}
