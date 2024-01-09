package core.excel.excel_data.bills.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BillExcel {

    private String organization;
    private List<BillExcelItem> rows;
}
