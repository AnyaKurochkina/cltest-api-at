package ui.t1.pages.cloudEngine.vpc;

import core.utils.Waiting;
import lombok.Getter;
import ui.elements.*;

import java.time.Duration;

import static core.helper.StringUtils.$x;
import static ui.t1.pages.cloudEngine.vpc.SecurityGroup.RulesTable.COLUMN_DESC;

@Getter
public class SecurityGroup {
    Button btnAddRule = Button.byElement($x("//button[.='Добавить']"));

    public Rule addRule() {
        btnAddRule.click();
        return new Rule();
    }

    public void deleteRule(String rule) {
        RulesTable.removeRule(rule).click();
        Waiting.findWidthRefresh(() -> !new RulesTable().isColumnValueEquals(COLUMN_DESC, rule), Duration.ofMinutes(1));
    }


    public static class RulesTable extends Table {
        static final String COLUMN_DESC = "Описание";
        static final String COLUMN_STATUS = "Статус";

        public RulesTable() {
            super(COLUMN_DESC);
        }

        public static Row getRule(String rule) {
            return new RulesTable().getRowByColumnValue(COLUMN_DESC, rule);
        }

        public static Button removeRule(String rule) {
            return Button.byElement(getRule(rule).get().$("button"));
        }
    }
}
