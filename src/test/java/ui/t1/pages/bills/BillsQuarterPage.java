package ui.t1.pages.bills;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Step;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ui.elements.Select;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BillsQuarterPage extends AbstractBillsPeriodPage {

    private final Select quarterSelect = Select.byLabel("Квартал");

    public static BillsQuarterPage init() {
        return Selenide.page(BillsQuarterPage.class);
    }

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
