package core.excel.excel_core.sheet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class RowObject {
    private Integer rowNum;
    private List<CellObject> cells;
    @Builder.Default
    private Integer height = 15;
}
