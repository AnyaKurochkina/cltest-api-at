package core.excel.excel_data.bills.model;

import core.excel.excel_core.field.AbstractExcelFields;
import core.excel.excel_core.field.ExcelField;
import core.excel.excel_core.field.VtbExcelField;
import core.excel.excel_core.field.VtbFieldInfo;
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
                    new VtbExcelField<>(VtbFieldInfo.PROJECT, VtbFieldInfo.SERVICE, BillExcel::getProject),
                    new VtbExcelField<>(VtbFieldInfo.SERVICE, VtbFieldInfo.TARIFF_CLASS_OF_SERVICE, BillExcel::getService),
                    new VtbExcelField<>(VtbFieldInfo.TARIFF_CLASS_OF_SERVICE, VtbFieldInfo.START_DATE, BillExcel::getTariffClassOfService),
                    new VtbExcelField<>(VtbFieldInfo.START_DATE, VtbFieldInfo.END_DATE, BillExcel::getStartDate),
                    new VtbExcelField<>(VtbFieldInfo.END_DATE, VtbFieldInfo.AMOUNT_OF_DAYS_IN_PERIOD, BillExcel::getEndDate),
                    new VtbExcelField<>(VtbFieldInfo.AMOUNT_OF_DAYS_IN_PERIOD, VtbFieldInfo.UNIT_OF_MEASURING, BillExcel::getAmountOfDaysInPeriod),
                    new VtbExcelField<>(VtbFieldInfo.UNIT_OF_MEASURING, VtbFieldInfo.UNIT_OF_CALCULATION, BillExcel::getUnitOfMeasuring),
                    new VtbExcelField<>(VtbFieldInfo.UNIT_OF_CALCULATION, VtbFieldInfo.SUM_CONSUMING_IN_DAYS, BillExcel::getUnitOfCalculation),
                    new VtbExcelField<>(VtbFieldInfo.SUM_CONSUMING_IN_DAYS, VtbFieldInfo.VOLUME, BillExcel::getSumConsumingInDays),
                    new VtbExcelField<>(VtbFieldInfo.VOLUME, VtbFieldInfo.PRICE_FOR_DAY_WITHOUT_TAX, BillExcel::getVolume),
                    new VtbExcelField<>(VtbFieldInfo.PRICE_FOR_DAY_WITHOUT_TAX, VtbFieldInfo.PRICE_FOR_DAY_WITH_TAX, BillExcel::getPriceForDayWithoutTax),
                    new VtbExcelField<>(VtbFieldInfo.PRICE_FOR_DAY_WITH_TAX, VtbFieldInfo.SUM_WITHOUT_TAX, BillExcel::getPriceForDayWithTax),
                    new VtbExcelField<>(VtbFieldInfo.SUM_WITHOUT_TAX, VtbFieldInfo.SUM_WITH_TAX, BillExcel::getSumWithoutTax),
                    new VtbExcelField<>(VtbFieldInfo.SUM_WITH_TAX, BillExcel::getSumWithTax)
            )));
        }
    }
}
