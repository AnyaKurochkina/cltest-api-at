package core.helper;

import core.utils.AssertUtils;
import org.junit.jupiter.api.Assertions;
import ui.elements.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class TableChecker {
    private final List<String> headers = new ArrayList<>();
    private final List<Predicate<String>> conditions = new ArrayList<>();

    public TableChecker add(String column, Predicate<String> rule){
        this.headers.add(column);
        this.conditions.add(rule);
        return this;
    }

    public void check(Supplier<Table.Row> row) {
        AssertUtils.assertHeaders(row.get().getTable(), headers);
        for (int i = 0; i < headers.size(); i++) {
            String value = row.get().getValueByColumn(headers.get(i));
            Assertions.assertTrue(conditions.get(i).test(value), String.format("Значение '%s' в колонке '%s' не соответствует условию", value, headers.get(i)));
        }
    }
}
