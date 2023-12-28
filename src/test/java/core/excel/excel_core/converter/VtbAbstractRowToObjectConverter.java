package core.excel.excel_core.converter;

import core.excel.excel_core.field.ExcelField;
import core.excel.excel_core.field.VtbFieldInfo;

import java.util.Map;

public abstract class VtbAbstractRowToObjectConverter<T> extends AbstractRowToObjectConverter<T> {

    public VtbAbstractRowToObjectConverter(Map<Integer, ExcelField<T>> fieldsMapping) {
        super(fieldsMapping);
    }

    protected Integer getCellNum(VtbFieldInfo field) {
        return getCellNumByField(field, fieldsMapping);
    }

    private Integer getCellNumByField(VtbFieldInfo fieldInfo, Map<Integer, ExcelField<T>> mapping) {
        return mapping.entrySet().parallelStream()
                .filter(field -> field.getValue().getFieldName().equals(fieldInfo.getName()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException(String.format("Не найден номер ячейки для ячейки %s", fieldInfo.name())));
    }
}
