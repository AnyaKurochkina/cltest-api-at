package core.excel.excel_data.bills.model;

import core.excel.excel_core.field.AbstractExcelFields;
import core.excel.excel_core.field.ExcelField;
import core.excel.excel_core.field.ExcelFieldWithMapping;
import core.excel.excel_core.field.FieldInfoMapping;
import core.excel.excel_core.sheet.AbstractExcelSheet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

public class BillExcelSheet extends AbstractExcelSheet<BillExcel> {
    private static final String SHEET_TITLE = "Услуги";
    private static final int HEADER_ROWS = 1;
    private static final Map<Integer, ExcelField<BillExcel>> FIELDS_MAPPING = new BillExcelFields().getHeaderFieldsMappings();

    public BillExcelSheet() {
        super(HEADER_ROWS, SHEET_TITLE, FIELDS_MAPPING);
    }

    public static class BillExcelFields extends AbstractExcelFields<BillExcel> {
        @Override
        public Map<Integer, ExcelField<BillExcel>> getHeaderFieldsMappings() {
            return createFieldsMappings(new HashSet<>(Arrays.asList(
                    new ExcelFieldWithMapping<>(FieldInfoMapping.PROJECT, FieldInfoMapping.SERVICE, BillExcel::getProject),
                    new ExcelFieldWithMapping<>(FieldInfoMapping.SERVICE, FieldInfoMapping.TARIFF_CLASS_OF_SERVICE, BillExcel::getService),
                    new ExcelFieldWithMapping<>(FieldInfoMapping.TARIFF_CLASS_OF_SERVICE, FieldInfoMapping.START_DATE, BillExcel::getTariffClassOfService),
                    new ExcelFieldWithMapping<>(FieldInfoMapping.START_DATE, FieldInfoMapping.END_DATE, BillExcel::getStartDate),
                    new ExcelFieldWithMapping<>(FieldInfoMapping.END_DATE, FieldInfoMapping.AMOUNT_OF_DAYS_IN_PERIOD, BillExcel::getEndDate),
                    new ExcelFieldWithMapping<>(FieldInfoMapping.AMOUNT_OF_DAYS_IN_PERIOD, FieldInfoMapping.UNIT_OF_MEASURING, BillExcel::getAmountOfDaysInPeriod),
                    new ExcelFieldWithMapping<>(FieldInfoMapping.UNIT_OF_MEASURING, FieldInfoMapping.UNIT_OF_CALCULATION, BillExcel::getUnitOfMeasuring),
                    new ExcelFieldWithMapping<>(FieldInfoMapping.UNIT_OF_CALCULATION, FieldInfoMapping.SUM_CONSUMING_IN_DAYS, BillExcel::getUnitOfCalculation),
                    new ExcelFieldWithMapping<>(FieldInfoMapping.SUM_CONSUMING_IN_DAYS, FieldInfoMapping.VOLUME, BillExcel::getSumConsumingInDays),
                    new ExcelFieldWithMapping<>(FieldInfoMapping.VOLUME, FieldInfoMapping.PRICE_FOR_DAY_WITHOUT_TAX, BillExcel::getVolume),
                    new ExcelFieldWithMapping<>(FieldInfoMapping.PRICE_FOR_DAY_WITHOUT_TAX, FieldInfoMapping.PRICE_FOR_DAY_WITH_TAX, BillExcel::getPriceForDayWithoutTax),
                    new ExcelFieldWithMapping<>(FieldInfoMapping.PRICE_FOR_DAY_WITH_TAX, FieldInfoMapping.SUM_WITHOUT_TAX, BillExcel::getPriceForDayWithTax),
                    new ExcelFieldWithMapping<>(FieldInfoMapping.SUM_WITHOUT_TAX, FieldInfoMapping.SUM_WITH_TAX, BillExcel::getSumWithoutTax),
                    new ExcelFieldWithMapping<>(FieldInfoMapping.SUM_WITH_TAX, BillExcel::getSumWithTax)
            )));
        }
    }
}
