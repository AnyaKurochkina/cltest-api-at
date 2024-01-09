package core.excel.excel_core.field;

import lombok.Getter;

import java.util.function.Function;

@Getter
public class ExcelFieldWithMapping<T> extends ExcelField<T> {

    public ExcelFieldWithMapping(FieldInfoMapping fieldInfo, FieldInfoMapping nextField, Function<T, String> fieldMappingFunc) {
        super(fieldInfo.getName(), nextField.getName(), fieldMappingFunc);
    }

    public ExcelFieldWithMapping(FieldInfoMapping fieldInfo, Function<T, String> fieldMappingFunc) {
        super(fieldInfo.getName(), null, fieldMappingFunc);
    }
}
