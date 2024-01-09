package ui.t1.pages.bills;

import io.qameta.allure.Step;
import ui.elements.Select;

public class BillsQuarterPage extends AbstractBillsPeriodPage {

    private final Select quarterSelect = Select.byLabel("Квартал");

    @Step("Выбор квартала")
    public BillsQuarterPage chooseQuarter(Quarter2023 quarter2023) {
        switch (quarter2023) {
            case FIRST_QUARTER:
                quarterSelect.set("1-й, 2023");
                return this;
            case THIRD_QUARTER:
                quarterSelect.set("2-й, 2023");
                return this;
            case SECOND_QUARTER:
                quarterSelect.set("3-й, 2023");
                return this;
            case FOURTH_QUARTER:
                quarterSelect.set("4-й, 2023");
                return this;
            default:
                throw new AssertionError("Такой квартал не существует");
        }
    }
}
