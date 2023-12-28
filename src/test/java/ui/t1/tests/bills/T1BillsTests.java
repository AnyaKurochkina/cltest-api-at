package ui.t1.tests.bills;

import api.Tests;
import core.enums.Role;
import core.excel.excel_data.bills.BillExcelReader;
import core.excel.excel_data.bills.model.BillExcel;
import core.utils.DownloadingFilesUtil;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import org.junit.EnabledIfEnv;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.T1LoginPage;
import ui.t1.pages.bills.RuMonth;

import java.io.File;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static core.utils.DownloadingFilesUtil.DOWNLOADS_DIRECTORY_PATH;

@ExtendWith(ConfigExtension.class)
@Tags({@Tag("bills"), @Tag("t1")})
@Epic("Счета")
@Feature("Счета")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class T1BillsTests extends Tests {

    // DateTimeFormatter с учетом русского языка и шаблона 03-мар-2023
    private static final DateTimeFormatter LITERAL_MONTH_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy", new Locale("ru"));
    private final LocalDate expectedStartDateCustomPeriod = LocalDate.of(2023, Month.MARCH, 1);
    private final LocalDate expectedEndDateCustomPeriod = LocalDate.of(2023, Month.APRIL, 1);
    private final LocalDate expectedStartDateNovemberPeriod = LocalDate.of(2023, Month.NOVEMBER, 1);
    private final LocalDate expectedEndDateNovemberPeriod = LocalDate.of(2023, Month.NOVEMBER, 30);
    private final String expectedPeriod = expectedStartDateCustomPeriod + " - " + expectedEndDateCustomPeriod;
    private final String expectedNovemberPeriod = LocalDate.of(2023, Month.NOVEMBER, 1) + " - " + LocalDate.of(2023, Month.NOVEMBER, 30);
    private final Project project = Project.builder().isForOrders(true).build().createObject();

    @BeforeEach
    public void singIn() {
        new T1LoginPage(project.getId()).signIn(Role.SUPERADMIN);
    }

    //На dev почемуто отсутствуют креды Role.SUPERADMIN
    @EnabledIfEnv("t1ift")
    @Test
    @TmsLink("SOUL-3391")
    @DisplayName("Счета. Скачать данные за месяц")
    void downloadBillExcelForOneMonthTest() {
        String expectedFileNameWithNovemberPeriod = prepareFileName(expectedStartDateNovemberPeriod, expectedEndDateNovemberPeriod, "ift");
        new IndexPage().goToBillsPage()
                .goToMonthPeriod()
                .chooseMontWithYear(RuMonth.NOVEMBER, "2023")
                .clickExport();

        checkPeriodInExcelFile(expectedNovemberPeriod, expectedFileNameWithNovemberPeriod);
    }

    @EnabledIfEnv("t1ift")
    @Test
    @TmsLink("SOUL-3393")
    @DisplayName("Счета. Скачать данные. Интервал")
    void downloadBillExcelCustomPeriodTest() {
        String expectedFileNameWithCustomPeriod = prepareFileName(expectedStartDateCustomPeriod, expectedEndDateCustomPeriod, "ift");
        new IndexPage().goToBillsPage()
                .goToCustomPeriod()
                .setPeriod(expectedStartDateCustomPeriod, expectedEndDateCustomPeriod)
                .clickExport();

        checkPeriodInExcelFile(expectedPeriod, expectedFileNameWithCustomPeriod);
    }

    @Step("[Проверка] Период счета в файле excel соответсвует периоду выбранному при выгрузке отчета")
    private void checkPeriodInExcelFile(String expectedPeriod, String fileName) {
        DownloadingFilesUtil.checkFileExistsInDownloadsDirectory(fileName);
        BillExcel randomBill = getRandomRowFromBillExcelFile(fileName);
        String actualPeriod = createPeriod(randomBill);
        Assertions.assertEquals(expectedPeriod, actualPeriod,
                "Период счета в файле excel должен соответсвовать периоду выбранному при выгрузке отчета");

    }

    @Step("Создание периода для проверки в виде 03.03.2023 - 03.04.2023")
    private static String createPeriod(BillExcel randomBill) {
        return convertIntoLocalDate(randomBill.getStartDate()) + " - " + convertIntoLocalDate(randomBill.getEndDate());
    }

    @Step("Получение рандомной строчки из excel документа 'Счета'")
    private static BillExcel getRandomRowFromBillExcelFile(String expectedFileName) {
        return new BillExcelReader(new File(DOWNLOADS_DIRECTORY_PATH + expectedFileName)).read()
                .stream()
                .findAny()
                .orElseThrow(() -> new AssertionError("В файле excel не найдено ниодной строчки"));
    }

    /**
     * Имя файла необходимо параметризировать чтобы получился формат: "user_bills_from_2023-03-01_till_2023-04-01_for_ift.xlsx"
     * где:
     * 1) %s - Начало периода
     * 2) %s - Окончание периода
     * 3) %s - Название организации
     */
    @Step("Подготовка имени файла в формате: user_bills_from_2023-03-01_till_2023-04-01_for_ift")
    private static String prepareFileName(LocalDate expectedStartDateCustomPeriod, LocalDate expectedEndDateCustomPeriod, String organizationName) {
        return String.format("user_bills_from_%s_till_%s_for_%s.xlsx", expectedStartDateCustomPeriod, expectedEndDateCustomPeriod, organizationName);
    }

    public static LocalDate convertIntoLocalDate(String stringDate) {
        return LocalDate.parse(stringDate, LITERAL_MONTH_FORMATTER);
    }
}
