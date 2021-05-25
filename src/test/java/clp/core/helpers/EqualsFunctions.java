package clp.core.helpers;

import java.util.function.BiPredicate;

/**
 * Фабрика функций BiPredicate сравнения
 * Применять для варификации в логах или ассертах
 *
 * @author kochetkovma
 */
public class EqualsFunctions {

    private EqualsFunctions() {}

    /**
     * BiPredicate<String, String> (expected, actual) сравнения строк без пробелов и игнорирует кейс
     *
     * @return
     */
    public static BiPredicate<String, String> newStringEqualsCaseSpaceIgnore() {
        return (expected, actual) -> actual.replaceAll("\\s", "").equalsIgnoreCase(expected.replaceAll("\\s", ""));
    }

    /**
     * BiPredicate<String, String> (expected, actual) содержится ли строка expected в строке actual без пробелов и игнорирует кейс
     *
     * @return
     */
    public static BiPredicate<String, String> newStringContainsCaseSpaceIgnore() {
        return (expected, actual) -> {
            return actual.replaceAll("\\s", "").toLowerCase().contains(expected.replaceAll("\\s", "").toLowerCase());
        };
    }


    /**
     * BiPredicate<String, String> (expected, actual) Сравнивает actual строку с регулярным выражением expected
     *
     * @return
     */
    public static BiPredicate<String, String> newRegexpEquals() {
        return (expected, actual) -> actual.matches(expected);
    }

}
