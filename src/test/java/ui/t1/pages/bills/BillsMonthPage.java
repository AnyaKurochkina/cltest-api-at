package ui.t1.pages.bills;

import com.codeborne.selenide.Selenide;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ui.elements.Select;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BillsMonthPage extends AbstractBillsPeriodPage {

    private final Select monthSelect = Select.byLabel("Месяц");

    public static BillsMonthPage init() {
        return Selenide.page(BillsMonthPage.class);
    }

    public BillsMonthPage chooseMontWithYear(RuMonth month, String year) {
        monthSelect.set(month.getUiValue() + ", " + year);
        return this;
    }
}
