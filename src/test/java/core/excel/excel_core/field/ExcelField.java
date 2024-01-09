package core.excel.excel_core.field;

import lombok.Getter;

import java.util.Objects;
import java.util.function.Function;

@Getter
public class ExcelField<T> {

    private final String fieldName;
    private final String nextFieldName;
    private final Function<T, String> fieldMappingFunc;

    public ExcelField(String fieldName, String nextFieldName, Function<T, String> fieldMappingFunc) {
        this.fieldName = fieldName;
        this.nextFieldName = nextFieldName;
        this.fieldMappingFunc = fieldMappingFunc;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof ExcelField)) return false;
        ExcelField<T> f = (ExcelField<T>) obj;
        return this.fieldName.equals(f.getFieldName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldName);
    }
}
