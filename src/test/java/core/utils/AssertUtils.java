package core.utils;

import org.junit.jupiter.api.Assertions;
import ui.elements.Table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.fail;

public class AssertUtils {

    public static void AssertDate(Date expected, Date actual, int deltaSec, String message) {
        long diff = TimeUnit.SECONDS.convert(Math.abs(expected.getTime() - actual.getTime()), TimeUnit.MILLISECONDS);
        Assertions.assertTrue(diff <= deltaSec, message);
    }

    public static void assertHeaders(Table table, String... headers) {
        Assertions.assertEquals(Arrays.asList(headers), table.getHeaders(), "Названия столбцов в таблице не совпадают");
    }

    public static void assertEqualsList(List<String> l1, List<String> l2) {
        List<String> differences = new ArrayList<>(l1);
        differences.removeAll(l2);
        if (!differences.isEmpty()) {
            fail(String.format("Not equals:\n%s\n%s", Arrays.toString(l1.toArray()), Arrays.toString(l2.toArray())));
        }
    }

    public static void assertContains(String text, String... strings) {
        for (String str : strings)
            if (!text.contains(str)) {
                fail(String.format("The text '%s' not found in '%s'", str, text));
            }
    }
}
