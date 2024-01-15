package core.excel.excel_core.sheet;

import core.excel.excel_core.field.ExcelField;
import lombok.Getter;

import java.util.Map;

@Getter
public abstract class AbstractExcelSheet<T> {

    protected final Integer headerRows;
    protected final String sheetTitle;
    protected final Map<Integer, ExcelField<T>> fieldsMapping;

    public AbstractExcelSheet(Integer headerRows, String sheetTitle, Map<Integer, ExcelField<T>> fieldsMapping) {
        this.headerRows = headerRows;
        this.sheetTitle = sheetTitle;
        this.fieldsMapping = fieldsMapping;
    }
}
