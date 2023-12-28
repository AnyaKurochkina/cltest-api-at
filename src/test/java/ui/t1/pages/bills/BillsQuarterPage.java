package ui.t1.pages.bills;

import com.codeborne.selenide.Selenide;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BillsQuarterPage extends AbstractBillsPeriodPage {

    public static BillsQuarterPage init() {
        return Selenide.page(BillsQuarterPage.class);
    }
}
