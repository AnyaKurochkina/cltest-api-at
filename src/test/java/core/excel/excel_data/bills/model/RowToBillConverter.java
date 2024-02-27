package core.excel.excel_data.bills.model;

import core.excel.excel_core.ExcelReaderUtil;
import core.excel.excel_core.converter.AbstractRowToObjectConverter;
import core.excel.excel_core.field.ExcelField;
import core.excel.excel_core.field.FieldInfoMapping;
import org.apache.poi.ss.usermodel.Row;

import java.util.Map;

public class RowToBillConverter extends AbstractRowToObjectConverter<BillExcelItem> {

    private int rowNum;

    public RowToBillConverter(Map<Integer, ExcelField<BillExcelItem>> fieldsMapping) {
        super(fieldsMapping);
    }

    @Override
    public BillExcelItem convert(Row row) {
        return BillExcelItem.builder().rowNum(rowNum++)
                .project(ExcelReaderUtil.readStringCell(row, getCellNum(FieldInfoMapping.PROJECT)))
                .order(ExcelReaderUtil.readStringCell(row, getCellNum(FieldInfoMapping.ORDER)))
                .service(ExcelReaderUtil.readStringCell(row, getCellNum(FieldInfoMapping.SERVICE)))
                .tariffClassOfService(ExcelReaderUtil.readStringCell(row, getCellNum(FieldInfoMapping.TARIFF_CLASS_OF_SERVICE)))
                .startDate(ExcelReaderUtil.readStringCell(row, getCellNum(FieldInfoMapping.START_DATE)))
                .endDate(ExcelReaderUtil.readStringCell(row, getCellNum(FieldInfoMapping.END_DATE)))
                .amountOfDaysInPeriod(ExcelReaderUtil.readStringCell(row, getCellNum(FieldInfoMapping.AMOUNT_OF_DAYS_IN_PERIOD)))
                .unitOfMeasuring(ExcelReaderUtil.readStringCell(row, getCellNum(FieldInfoMapping.UNIT_OF_MEASURING)))
                .unitOfCalculation(ExcelReaderUtil.readStringCell(row, getCellNum(FieldInfoMapping.UNIT_OF_CALCULATION)))
                .sumConsumingInDays(ExcelReaderUtil.readStringCell(row, getCellNum(FieldInfoMapping.SUM_CONSUMING_IN_DAYS)))
                .volume(ExcelReaderUtil.readStringCell(row, getCellNum(FieldInfoMapping.VOLUME)))
                .priceForDayWithoutTax(ExcelReaderUtil.readStringCell(row, getCellNum(FieldInfoMapping.PRICE_FOR_DAY_WITHOUT_TAX)))
                .priceForDayWithTax(ExcelReaderUtil.readStringCell(row, getCellNum(FieldInfoMapping.PRICE_FOR_DAY_WITH_TAX)))
                .sumWithoutTax(ExcelReaderUtil.readStringCell(row, getCellNum(FieldInfoMapping.SUM_WITHOUT_TAX)))
                .sumWithTax(ExcelReaderUtil.readStringCell(row, getCellNum(FieldInfoMapping.SUM_WITH_TAX)))
                .build();
    }
}