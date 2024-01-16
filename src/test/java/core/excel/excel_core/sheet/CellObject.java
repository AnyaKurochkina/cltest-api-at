package core.excel.excel_core.sheet;

import lombok.Data;

@Data
public class CellObject {

    private Integer columnIndex;
    private Object value;
    private Integer height;

    public CellObject(Integer columnIndex, Object value) {
        this.columnIndex = columnIndex;
        this.value = value;
    }
}
