package core.utils;

import org.junit.jupiter.api.Assertions;
import ui.elements.Table;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AssertUtils {

    public static void AssertDate(Date expected, Date actual, int deltaSec, String message) {
        long diff = TimeUnit.SECONDS.convert(Math.abs(expected.getTime() - actual.getTime()), TimeUnit.MILLISECONDS);
        Assertions.assertTrue(diff <= deltaSec, message);
    }

    public static void assertHeaders(Table table, String... headers) {
        Assertions.assertEquals(Arrays.asList(headers), table.getHeaders(), "Названия столбцов в таблице не совпадают");
    }
}
