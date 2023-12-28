package core.excel.excel_data.bills.model;

import core.excel.excel_core.ExcelReaderUtil;
import core.excel.excel_core.converter.VtbAbstractRowToObjectConverter;
import core.excel.excel_core.field.ExcelField;
import core.excel.excel_core.field.VtbFieldInfo;
import org.apache.poi.ss.usermodel.Row;

import java.util.Map;

public class RowToBillConverter extends VtbAbstractRowToObjectConverter<BillExcel> {

    private int rowNum;

    public RowToBillConverter(Map<Integer, ExcelField<BillExcel>> fieldsMapping) {
        super(fieldsMapping);
    }

    @Override
    public BillExcel convert(Row row) {
        return BillExcel.builder().rowNum(rowNum++)
                .project(ExcelReaderUtil.readStringCell(row, getCellNum(VtbFieldInfo.PROJECT)))
                .service(ExcelReaderUtil.readStringCell(row, getCellNum(VtbFieldInfo.SERVICE)))
                .tariffClassOfService(ExcelReaderUtil.readStringCell(row, getCellNum(VtbFieldInfo.TARIFF_CLASS_OF_SERVICE)))
                .startDate(ExcelReaderUtil.readStringCell(row, getCellNum(VtbFieldInfo.START_DATE)))
                .endDate(ExcelReaderUtil.readStringCell(row, getCellNum(VtbFieldInfo.END_DATE)))
                .amountOfDaysInPeriod(ExcelReaderUtil.readStringCell(row, getCellNum(VtbFieldInfo.AMOUNT_OF_DAYS_IN_PERIOD)))
                .unitOfMeasuring(ExcelReaderUtil.readStringCell(row, getCellNum(VtbFieldInfo.UNIT_OF_MEASURING)))
                .unitOfCalculation(ExcelReaderUtil.readStringCell(row, getCellNum(VtbFieldInfo.UNIT_OF_CALCULATION)))
                .sumConsumingInDays(ExcelReaderUtil.readStringCell(row, getCellNum(VtbFieldInfo.SUM_CONSUMING_IN_DAYS)))
                .volume(ExcelReaderUtil.readStringCell(row, getCellNum(VtbFieldInfo.VOLUME)))
                .priceForDayWithoutTax(ExcelReaderUtil.readStringCell(row, getCellNum(VtbFieldInfo.PRICE_FOR_DAY_WITHOUT_TAX)))
                .priceForDayWithTax(ExcelReaderUtil.readStringCell(row, getCellNum(VtbFieldInfo.PRICE_FOR_DAY_WITH_TAX)))
                .sumWithoutTax(ExcelReaderUtil.readStringCell(row, getCellNum(VtbFieldInfo.SUM_WITHOUT_TAX)))
                .sumWithTax(ExcelReaderUtil.readStringCell(row, getCellNum(VtbFieldInfo.SUM_WITH_TAX)))
                .build();
    }
}