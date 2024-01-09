package ui.t1.pages.bills;

import io.qameta.allure.Step;
import ui.elements.Button;

public class BillsPage {

    private final Button byMonthButton = Button.byText("Месяц");
    private final Button byQuarterButton = Button.byText("Квартал");
    private final Button byPeriodButton = Button.byText("Период");

    @Step("Переход к счетам за месяц")
    public BillsMonthPage goToMonthPeriod() {
        byMonthButton.click();
        return new BillsMonthPage();
    }

    @Step("Переход к счетам за квартал")
    public BillsQuarterPage goToQuarterPeriod() {
        byQuarterButton.click();
        return new BillsQuarterPage();
    }

    @Step("Переход к счетам за выбранный период")
    public BillsPeriodPage goToCustomPeriod() {
        byPeriodButton.click();
        return new BillsPeriodPage();
    }
}
