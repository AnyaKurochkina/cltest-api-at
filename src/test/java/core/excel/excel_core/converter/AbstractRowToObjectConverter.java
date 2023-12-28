package core.excel.excel_core.converter;


import core.excel.excel_core.field.ExcelField;

import java.util.Map;

public abstract class AbstractRowToObjectConverter<T> implements RowToObjectConverter<T> {

    protected final Map<Integer, ExcelField<T>> fieldsMapping;

    protected AbstractRowToObjectConverter(Map<Integer, ExcelField<T>> fieldsMapping) {
        this.fieldsMapping = fieldsMapping;
    }
}
