package ui.t1.pages.bills;

import io.qameta.allure.Step;
import ui.elements.Input;
import ui.elements.Select;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class BillsPeriodPage extends AbstractBillsPeriodPage<BillsPeriodPage> {

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final Select organiztionSelect = Select.byLabel("Период");
    private final Input startDate = Input.byLabel("Начало");
    private final Input endDate = Input.byLabel("Окончание");

    /**
     * example: setPeriod(new DatePeriod(LocalDate.of(2023, Month.MARCH, 1), LocalDate.of(2023, Month.APRIL, 1)));
     *
     * @param datePeriod период дат
     */
    @Step("Установка периода")
    public BillsPeriodPage setPeriod(DatePeriod datePeriod) {
        startDate.setValue(LocalDateTime.of(datePeriod.getStartDate(), LocalTime.now()).format(dateFormat));
        endDate.setValue(LocalDateTime.of(datePeriod.getEndDate(), LocalTime.now()).format(dateFormat));
        return this;
    }
}
