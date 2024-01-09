package core.excel.excel_core.converter;

import org.apache.poi.ss.usermodel.Row;

public interface RowToObjectConverter<T> {

    T convert(Row row);
}
