package core.excel.excel_data.bills;

import core.excel.excel_core.reader.AbstractExcelReader;
import core.excel.excel_core.sheet.AbstractExcelSheet;
import core.excel.excel_data.bills.model.BillExcel;
import core.excel.excel_data.bills.model.BillExcelItem;
import core.excel.excel_data.bills.model.BillExcelSheet;
import core.excel.excel_data.bills.model.RowToBillConverter;
import io.qameta.allure.Step;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class BillExcelReader extends AbstractExcelReader<BillExcelItem> {

    private static final AbstractExcelSheet<BillExcelItem> READER_SHEET = new BillExcelSheet();

    public BillExcelReader(File file) {
        super(file, new RowToBillConverter(READER_SHEET.getFieldsMapping()), READER_SHEET);
    }

    @Override
    public <X extends AbstractExcelReader<?>> X createReaderFromFile(File file) {
        return (X) new BillExcelReader(file);
    }

    @Override
    @Step("Чтение excel файла 'Счета'")
    public List<BillExcelItem> read() {
        return getContentRows().stream()
                .skip(5)
                .collect(Collectors.toList());
    }

    @Step("Чтение excel файла 'Счета'")
    public BillExcel readWithOrganization() {
        return BillExcel.builder()
                .organization(getContentRows().get(0).getOrder())
                .rows(read())
                .build();
    }
}