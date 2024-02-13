package core.excel.excel_data.bills.model;

import core.excel.excel_core.field.AbstractExcelFields;
import core.excel.excel_core.field.ExcelField;
import core.excel.excel_core.field.ExcelFieldWithMapping;
import core.excel.excel_core.field.FieldInfoMapping;
import core.excel.excel_core.sheet.AbstractExcelSheet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

public class BillExcelSheet extends AbstractExcelSheet<BillExcelItem> {
    private static final String SHEET_TITLE = "Услуги";
    private static final int HEADER_ROWS = 0;
    private static final Map<Integer, ExcelField<BillExcelItem>> FIELDS_MAPPING = new BillExcelFields().getHeaderFieldsMappings();

    public BillExcelSheet() {
        super(HEADER_ROWS, SHEET_TITLE, FIELDS_MAPPING);
    }

    public static class BillExcelFields extends AbstractExcelFields<BillExcelItem> {
        @Override
        public Map<Integer, ExcelField<BillExcelItem>> getHeaderFieldsMappings() {
            return createFieldsMappings(new HashSet<>(Arrays.asList(
                    new ExcelFieldWithMapping<>(FieldInfoMapping.PROJECT, FieldInfoMapping.ORDER, BillExcelItem::getProject),
                    new ExcelFieldWithMapping<>(FieldInfoMapping.ORDER, FieldInfoMapping.SERVICE, BillExcelItem::getOrder),
                    new ExcelFieldWithMapping<>(FieldInfoMapping.SERVICE, FieldInfoMapping.TARIFF_CLASS_OF_SERVICE, BillExcelItem::getService),
                    new ExcelFieldWithMapping<>(FieldInfoMapping.TARIFF_CLASS_OF_SERVICE, FieldInfoMapping.START_DATE, BillExcelItem::getTariffClassOfService),
                    new ExcelFieldWithMapping<>(FieldInfoMapping.START_DATE, FieldInfoMapping.END_DATE, BillExcelItem::getStartDate),
                    new ExcelFieldWithMapping<>(FieldInfoMapping.END_DATE, FieldInfoMapping.AMOUNT_OF_DAYS_IN_PERIOD, BillExcelItem::getEndDate),
                    new ExcelFieldWithMapping<>(FieldInfoMapping.AMOUNT_OF_DAYS_IN_PERIOD, FieldInfoMapping.UNIT_OF_MEASURING, BillExcelItem::getAmountOfDaysInPeriod),
                    new ExcelFieldWithMapping<>(FieldInfoMapping.UNIT_OF_MEASURING, FieldInfoMapping.UNIT_OF_CALCULATION, BillExcelItem::getUnitOfMeasuring),
                    new ExcelFieldWithMapping<>(FieldInfoMapping.UNIT_OF_CALCULATION, FieldInfoMapping.SUM_CONSUMING_IN_DAYS, BillExcelItem::getUnitOfCalculation),
                    new ExcelFieldWithMapping<>(FieldInfoMapping.SUM_CONSUMING_IN_DAYS, FieldInfoMapping.VOLUME, BillExcelItem::getSumConsumingInDays),
                    new ExcelFieldWithMapping<>(FieldInfoMapping.VOLUME, FieldInfoMapping.PRICE_FOR_DAY_WITHOUT_TAX, BillExcelItem::getVolume),
                    new ExcelFieldWithMapping<>(FieldInfoMapping.PRICE_FOR_DAY_WITHOUT_TAX, FieldInfoMapping.PRICE_FOR_DAY_WITH_TAX, BillExcelItem::getPriceForDayWithoutTax),
                    new ExcelFieldWithMapping<>(FieldInfoMapping.PRICE_FOR_DAY_WITH_TAX, FieldInfoMapping.SUM_WITHOUT_TAX, BillExcelItem::getPriceForDayWithTax),
                    new ExcelFieldWithMapping<>(FieldInfoMapping.SUM_WITHOUT_TAX, FieldInfoMapping.SUM_WITH_TAX, BillExcelItem::getSumWithoutTax),
                    new ExcelFieldWithMapping<>(FieldInfoMapping.SUM_WITH_TAX, BillExcelItem::getSumWithTax)
            )));
        }
    }
}
