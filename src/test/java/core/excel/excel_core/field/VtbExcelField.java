package core.excel.excel_core.field;

import lombok.Getter;

import java.util.function.Function;

@Getter
public class VtbExcelField<T> extends ExcelField<T> {

    public VtbExcelField(VtbFieldInfo fieldInfo, VtbFieldInfo nextField, Function<T, String> fieldMappingFunc) {
        super(fieldInfo.getName(), nextField.getName(), fieldMappingFunc);
    }

    public VtbExcelField(VtbFieldInfo fieldInfo, Function<T, String> fieldMappingFunc) {
        super(fieldInfo.getName(), null, fieldMappingFunc);
    }
}
