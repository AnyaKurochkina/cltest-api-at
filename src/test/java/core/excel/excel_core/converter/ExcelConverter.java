package core.excel.excel_core.converter;

//import tools.utility.DateTimeHelper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Optional;

public abstract class ExcelConverter {

    protected static final String YES = "да";
    protected static final String NO = "нет";

    protected String getValue(Object value) {
        return Optional.ofNullable(value).map(Object::toString).orElse("");
    }

    protected String getNumberValue(Number value) {
        return new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US)).format(value);
    }

//    protected String getDate(Date date) {
//        return Optional.ofNullable(date).map(DateTimeHelper::makeStringFormat1).orElse("");
//    }
//
//    protected String getDate(LocalDate date) {
//        return Optional.ofNullable(date).map(DateTimeHelper::makeStringFormat1).orElse("");
//    }

    protected String getBoolean(Boolean value) {
        return Optional.ofNullable(value).map(val -> val ? YES : NO).orElse("");
    }

}
