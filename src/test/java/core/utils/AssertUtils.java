package core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.exception.NotFoundElementException;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import ui.elements.Table;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class AssertUtils {

    public static void AssertDate(Date expected, Date actual, int deltaSec, String message) {
        long diff = TimeUnit.SECONDS.convert(Math.abs(expected.getTime() - actual.getTime()), TimeUnit.MILLISECONDS);
        Assertions.assertTrue(diff <= deltaSec, message);
    }

    public static void assertHeaders(Table table, String... headers) {
        assertHeaders(table, Arrays.asList(headers));
    }

    public static void assertHeaders(Table table, Collection<String> headers) {
        assertEquals(headers, table.getHeaders(), "Названия столбцов в таблице не совпадают");
    }

    public static void assertEqualsList(List<String> l1, List<String> l2) {
        List<String> differences = new ArrayList<>(l1);
        differences.removeAll(l2);
        if (!differences.isEmpty()) {
            fail(String.format("Списки не совпадают:\n%s\n%s", Arrays.toString(l1.toArray()), Arrays.toString(l2.toArray())));
        }
    }

    @SneakyThrows
    public static void assertEqualsJson(JSONObject j1, JSONObject j2) {
        ObjectMapper mapper = new ObjectMapper();
        assertEquals(mapper.readTree(j1.toString()), mapper.readTree(j2.toString()));
    }

    public static void assertContainsList(List<?> list, Object... objects) {
        for (Object object : objects)
            if (!list.contains(object))
                throw new NotFoundElementException("Элемент {} не найден в списке \n{}", object, Arrays.toString(list.toArray()));
    }

    public static void assertNotContainsList(List<?> list, Object... objects) {
        for (Object object : objects)
            if (list.contains(object))
                throw new NotFoundElementException("Элемент {} найден в списке \n{}", object, Arrays.toString(list.toArray()));
    }

    public static void assertContains(String text, String... strings) {
        for (String str : strings)
            if (!text.contains(str)) {
                fail(String.format("Текст '%s' не найден в '%s'", str, text));
            }
    }
    public static void assertNotContains(String text, String... strings) {
        for (String str : strings)
            if (text.contains(str)) {
                fail(String.format("Текст '%s' найден в '%s'", str, text));
            }
    }
}
