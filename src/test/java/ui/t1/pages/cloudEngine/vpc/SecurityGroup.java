package ui.t1.pages.cloudEngine.vpc;

import core.utils.Waiting;
import lombok.Getter;
import ui.elements.*;
import ui.t1.pages.cloudEngine.Column;

import java.time.Duration;

import static core.helper.StringUtils.$x;

@Getter
public class SecurityGroup {
    Button btnAddRule = Button.byElement($x("//button[.='Добавить']"));

    public Rule addRule() {
        btnAddRule.click();
        return new Rule();
    }

    public void deleteRule(String rule) {
        RulesTable.removeRule(rule).click();
        Waiting.findWithRefresh(() -> !new RulesTable().isColumnValueEquals(Column.DESC, rule), Duration.ofMinutes(1));
    }


    public static class RulesTable extends Table {

        public RulesTable() {
            super(Column.DESC);
        }

        public static Row getRule(String rule) {
            return new RulesTable().getRowByColumnValue(Column.DESC, rule);
        }

        public static Button removeRule(String rule) {
            return Button.byElement(getRule(rule).get().$("button"));
        }
    }
}
