package ui.t1.pages.bills;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Month;

@AllArgsConstructor
@Getter
public enum Quarter2023 {

    FIRST_QUARTER(new DatePeriod(LocalDate.of(2023, Month.JANUARY, 1), LocalDate.of(2023, Month.MARCH, 31))),
    SECOND_QUARTER(new DatePeriod(LocalDate.of(2023, Month.APRIL, 1), LocalDate.of(2023, Month.JUNE, 30))),
    THIRD_QUARTER(new DatePeriod(LocalDate.of(2023, Month.JULY, 1), LocalDate.of(2023, Month.SEPTEMBER, 30))),
    FOURTH_QUARTER(new DatePeriod(LocalDate.of(2023, Month.OCTOBER, 1), LocalDate.of(2023, Month.DECEMBER, 31))),
    ;

    private final DatePeriod dateValue;
}
