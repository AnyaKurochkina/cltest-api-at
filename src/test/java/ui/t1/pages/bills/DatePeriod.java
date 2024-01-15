package ui.t1.pages.bills;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class DatePeriod {

    private LocalDate startDate;
    private LocalDate endDate;

    public String makePeriodString() {
        return startDate + " - " + endDate;
    }

    public DatePeriod(Quarter2023 quarter2023) {
        this.startDate = quarter2023.getDateValue().getStartDate();
        this.endDate = quarter2023.getDateValue().getEndDate();
    }
}
