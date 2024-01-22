package ui.t1.pages.bills;

import ui.elements.Select;

public class BillsMonthPage extends AbstractBillsPeriodPage<BillsMonthPage> {

    private final Select monthSelect = Select.byLabel("Месяц");

    public BillsMonthPage chooseMontWithYear(RuMonth month, String year) {
        monthSelect.set(month.getUiValue() + ", " + year);
        return this;
    }
}
