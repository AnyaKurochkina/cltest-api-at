package core.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtil {

    // DateTimeFormatter с учетом русского языка и шаблона 03-мар-2023
    private static final DateTimeFormatter LITERAL_MONTH_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy", new Locale("ru"));


    public static LocalDate convertIntoLocalDate(String stringDate) {
        return LocalDate.parse(stringDate, LITERAL_MONTH_FORMATTER);
    }
}
