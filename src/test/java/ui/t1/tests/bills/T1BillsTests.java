package ui.t1.tests.bills;

import api.Tests;
import core.enums.Role;
import core.utils.DownloadingFilesUtil;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.T1LoginPage;
import ui.t1.pages.bills.BillsPeriodPage;

import java.time.LocalDate;
import java.time.Month;

@ExtendWith(ConfigExtension.class)
@Tags({@Tag("bills"), @Tag("t1")})
@Epic("Счета")
@Feature("Счета")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class T1BillsTests extends Tests {

    private final Project project = Project.builder().isForOrders(true).build().createObject();

    @BeforeEach
    public void singIn() {
        new T1LoginPage(project.getId()).signIn(Role.SUPERADMIN);
    }

    @Test
    @TmsLink("SOUL-3390")
    @DisplayName("Счета. Скачать счет. Организация")
    void downloadOrganizationsBillTest() {
        new IndexPage().goToPortalBillsPage()
                .choosePeriodType(BillsPeriodPage.class)
                .setPeriod(LocalDate.of(2023, Month.MARCH, 3), LocalDate.of(2023, Month.APRIL, 3))
                .clickExport();

        DownloadingFilesUtil.checkFileExistsInDownloadsDirectory("user_bills_from_2023-03-03_till_2023-04-03_for_ift.xlsx");
        System.out.println();
    }
}
