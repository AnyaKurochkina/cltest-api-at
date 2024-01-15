package core.excel.excel_core.converter;


import core.excel.excel_core.field.ExcelField;
import core.excel.excel_core.field.FieldInfoMapping;

import java.util.Map;

public abstract class AbstractRowToObjectConverter<T> implements RowToObjectConverter<T> {

    protected final Map<Integer, ExcelField<T>> fieldsMapping;

    protected AbstractRowToObjectConverter(Map<Integer, ExcelField<T>> fieldsMapping) {
        this.fieldsMapping = fieldsMapping;
    }

    protected Integer getCellNum(FieldInfoMapping field) {
        return getCellNumByField(field, fieldsMapping);
    }

    private Integer getCellNumByField(FieldInfoMapping fieldInfo, Map<Integer, ExcelField<T>> mapping) {
        return mapping.entrySet().parallelStream()
                .filter(field -> field.getValue().getFieldName().equals(fieldInfo.getName()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException(String.format("Не найден номер ячейки для ячейки %s", fieldInfo.name())));
    }
}
