package ui.t1.pages.bills;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Step;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ui.elements.Input;
import ui.elements.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BillsPeriodPage extends AbstractBillsPeriodPage {

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final Select organiztionSelect = Select.byLabel("Период");
    private final Input startDate = Input.byLabel("Начало");
    private final Input endDate = Input.byLabel("Окончание");

    public static BillsPeriodPage init() {
        return Selenide.page(BillsPeriodPage.class);
    }

    /**
     * example: setPeriod(LocalDate.of(2023, Month.APRIL, 3), LocalDate.of(2023, Month.MARCH, 3));
     *
     * @param from от какой даты
     * @param to   до какой даты
     */
    @Step("Установка периода")
    public BillsPeriodPage setPeriod(LocalDate from, LocalDate to) {
        startDate.setValue(LocalDateTime.of(from, LocalTime.now()).format(dateFormat));
        endDate.setValue(LocalDateTime.of(to, LocalTime.now()).format(dateFormat));
        return this;
    }
}
