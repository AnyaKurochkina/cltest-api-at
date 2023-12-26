package ui.t1.tests.audit;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuditPeriod {

    LAST_HOUR("последний 1 час"),
    LAST_6_HOURS("последние 6 часов"),
    LAST_12_HOURS("последние 12 часов"),
    ONE_DAY("день"),
    WEEK("неделя"),
    SET_PERIOD("задать период");

    public final String uiValue;
}
