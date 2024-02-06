package ui.t1.tests.bills;

import core.enums.Role;
import core.excel.excel_data.bills.BillExcelReader;
import core.excel.excel_data.bills.model.BillExcel;
import core.excel.excel_data.bills.model.BillExcelItem;
import core.utils.DownloadingFilesUtil;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Organization;
import org.junit.EnabledIfEnv;
import org.junit.jupiter.api.*;
import ui.t1.pages.IndexPage;
import ui.t1.pages.bills.DatePeriod;
import ui.t1.pages.bills.Quarter2023;
import ui.t1.pages.bills.RuMonth;
import ui.t1.tests.AbstractT1Test;
import ui.t1.tests.WithAuthorization;

import java.io.File;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static core.utils.DownloadingFilesUtil.DOWNLOADS_DIRECTORY_PATH;

@WithAuthorization(Role.SUPERADMIN)
@Tags({@Tag("bills"), @Tag("t1")})
@Epic("Счета")
@Feature("Счета")
@Tag("morozov_ilya")
public class T1BillsTests extends AbstractT1Test {

    // DateTimeFormatter с учетом русского языка и шаблона 03-мар-2023
    private static final DateTimeFormatter LITERAL_MONTH_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy", new Locale("ru"));
    private final DatePeriod expectedCustomPeriod = new DatePeriod(LocalDate.of(2023, Month.MARCH, 1), LocalDate.of(2023, Month.APRIL, 1));
    private final DatePeriod expectedOctoberPeriod = new DatePeriod(LocalDate.of(2023, Month.OCTOBER, 1), LocalDate.of(2023, Month.OCTOBER, 31));
    private final Organization organization = Organization.builder().type("not_default").build().createObject();

    @EnabledIfEnv({"t1ift", "t1prod"})
    @Test
    @TmsLink("SOUL-3390")
    @DisplayName("Счета. Скачать счет. Организация")
    void downloadBillExcelForOneMonthAndCertainOrganizationTest() {
        new IndexPage().goToBillsPage()
                .goToMonthPeriod()
                .chooseOrganization(organization.getTitle())
                .chooseMontWithYear(RuMonth.NOVEMBER, "2023")
                .clickExport();

        checkOrganizationInExcelFile(organization.getTitle());
    }

    @EnabledIfEnv({"t1ift", "t1prod"})
    @Test
    @TmsLink("SOUL-3391")
    @DisplayName("Счета. Скачать данные за месяц")
    void downloadBillExcelForOneMonthTest() {
        new IndexPage().goToBillsPage()
                .goToMonthPeriod()
                .chooseMontWithYear(RuMonth.OCTOBER, "2023")
                .clickExport();

        checkPeriodInExcelFile(expectedOctoberPeriod.makePeriodString());
    }

    @EnabledIfEnv({"t1ift", "t1prod"})
    @Test
    @TmsLink("SOUL-3392")
    @DisplayName("Счета. Скачать данные за квартал")
    void downloadBillExcelForQuarterTest() {
        String expectedPeriod = new DatePeriod(Quarter2023.FIRST_QUARTER)
                .makePeriodString();
        new IndexPage().goToBillsPage()
                .goToQuarterPeriod()
                .chooseQuarter(Quarter2023.FIRST_QUARTER)
                .clickExport();

        checkPeriodInExcelFile(expectedPeriod);
    }

    @EnabledIfEnv({"t1ift", "t1prod"})
    @Test
    @TmsLink("SOUL-3393")
    @DisplayName("Счета. Скачать данные. Интервал")
    void downloadBillExcelCustomPeriodTest() {
        new IndexPage().goToBillsPage()
                .goToCustomPeriod()
                .setPeriod(expectedCustomPeriod)
                .clickExport();

        checkPeriodInExcelFile(expectedCustomPeriod.makePeriodString());
    }

    @EnabledIfEnv({"t1ift", "t1prod"})
    @Test
    @TmsLink("SOUL-7270")
    @DisplayName("Счета. Скачать счет. Выгрузка нулевых значений")
    void downloadBillExcelForOneMonthWithCheckboxTest() {
        new IndexPage().goToBillsPage()
                .goToMonthPeriod()
                .chooseMontWithYear(RuMonth.DECEMBER, "2023")
                .clickExportZeroPriceValuesCheckBox()
                .clickExport();

        checkExcelFileContainsBillWithZeroSumValue();
    }

    @Step("[Проверка] Период счета в файле excel соответсвует периоду выбранному при выгрузке отчета")
    private void checkPeriodInExcelFile(String expectedPeriod) {
        String fileName = DownloadingFilesUtil.getLastDownloadedFilename();
        DownloadingFilesUtil.checkFileExistsInDownloadsDirectory(fileName);
        BillExcelItem randomBill = getBillExcel(fileName).getRows()
                .stream()
                .findAny()
                .orElseThrow(() -> new AssertionError("В файле excel не найдено ниодной строчки"));

        String actualPeriod = createPeriod(randomBill);
        Assertions.assertEquals(expectedPeriod, actualPeriod,
                "Период счета в файле excel должен соответсвовать периоду выбранному при выгрузке отчета");

    }

    @Step("[Проверка] При выбранном чекбоксе 'Выгружать нулевые значения стоимости', в файле excel присутствовуют счета с нулевыми значениями стоимости")
    private void checkExcelFileContainsBillWithZeroSumValue() {
        String expectedFileName = DownloadingFilesUtil.getLastDownloadedFilename();
        DownloadingFilesUtil.checkFileExistsInDownloadsDirectory(expectedFileName);
        getBillExcel(expectedFileName).getRows().stream()
                .filter(bill -> bill.getSumWithoutTax().equals("0.0"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("При выбранном чекбоксе 'Выгружать нулевые значения стоимости', в файле excel должны присутствовать счета с нулевыми значениями стоимости"));
    }

    @Step("[Проверка] Организация в excel файле соответствует выбранной: {0}")
    private void checkOrganizationInExcelFile(String organizationName) {
        String fileName = DownloadingFilesUtil.getLastDownloadedFilename();
        DownloadingFilesUtil.checkFileExistsInDownloadsDirectory(fileName, 30);
        String organization = getBillExcel(fileName).getOrganization();

        Assertions.assertEquals(organizationName, organization,
                String.format("Организация в excel файле должна соответствовать выбранной: %s", organizationName));

    }

    @Step("Создание периода для проверки в виде 03.03.2023 - 03.04.2023")
    private static String createPeriod(BillExcelItem randomBill) {
        return convertIntoLocalDate(randomBill.getStartDate()) + " - " + convertIntoLocalDate(randomBill.getEndDate());
    }

    @Step("Получение excel документа 'Счета'")
    private static BillExcel getBillExcel(String expectedFileNameName) {
        return new BillExcelReader(new File(DOWNLOADS_DIRECTORY_PATH + expectedFileNameName))
                .readWithOrganization();
    }

    public static LocalDate convertIntoLocalDate(String stringDate) {
        return LocalDate.parse(stringDate, LITERAL_MONTH_FORMATTER);
    }
}
