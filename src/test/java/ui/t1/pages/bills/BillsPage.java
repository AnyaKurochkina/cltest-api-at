package ui.t1.pages.bills;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Step;
import ui.elements.Button;

public class BillsPage {

    private final Button byMonthButton = Button.byText("Месяц");
    private final Button byQuarterButton = Button.byText("Квартал");
    private final Button byPeriodButton = Button.byText("Период");

    /**
     * example: choosePeriodType(BillsMonthPage.class)
     *
     * @param billPage BillsPeriodPage.class - Период, BillsMonthPage - месяц, BillsQuarterPage - квартал
     * @param <T>      только классы которые наследуются от AbstractBillsPeriodPage
     * @return T extends AbstractBillsPeriodPage
     */
    @Step("Выбор типа периода")
    public <T extends AbstractBillsPeriodPage> T choosePeriodType(Class<T> billPage) {
        if (billPage.equals(BillsMonthPage.class)) {
            byMonthButton.click();
            return Selenide.page(billPage);
        } else if (billPage.equals(BillsQuarterPage.class)) {
            byQuarterButton.click();
            return Selenide.page(billPage);
        } else if (billPage.equals(BillsPeriodPage.class)) {
            byPeriodButton.click();
            return Selenide.page(billPage);
        }
        throw new AssertionError(String.format("Указан неверный период: %s", billPage.getName()));
    }
}
