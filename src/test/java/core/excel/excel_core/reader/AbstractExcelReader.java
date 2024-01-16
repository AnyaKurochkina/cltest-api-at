package core.excel.excel_core.reader;

import core.excel.excel_core.ExcelReaderUtil;
import core.excel.excel_core.converter.AbstractRowToObjectConverter;
import core.excel.excel_core.sheet.AbstractExcelSheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractExcelReader<T> {

    protected final Workbook workbook;
    protected final AbstractRowToObjectConverter<T> rowToObjectConverter;
    protected final AbstractExcelSheet<T> excelSheet;

    public AbstractExcelReader(File file, AbstractRowToObjectConverter<T> rowToObjectConverter, AbstractExcelSheet<T> excelSheet) {
        this.workbook = ExcelReaderUtil.getWorkbook(file);
        this.rowToObjectConverter = rowToObjectConverter;
        this.excelSheet = excelSheet;
    }

    /**
     * Метод преобразует excel файл в список объектов где каждый объект это строка из файла
     * Для реализации данного механизма необходимо:
     * 1) дополнить файл VtbFieldInfo.java нужными полями из вашего excel
     * 2) Реализовать YourExcel класс-модель в который вы хотите конвертировать excel файл
     * 3) Реализовать YourExcelSheet extends AbstractExcelSheet<YourExcel>  класс описывающий лист из excel
     * 4) Реализовать RowToYourClassConverter extends VtbAbstractRowToObjectConverter<YourExcel> который будет конвертировать
     * 5) Реализовать YourReader extends AbstractExcelReader<YourExcel>
     * Пример можно найти в src/test/java/ui/t1/tests/bills/excel_data/bills
     */
    public abstract List<T> read();

    public abstract <X extends AbstractExcelReader<?>> X createReaderFromFile(File file);

    protected List<Row> getRows() {
        return ExcelReaderUtil.getRows(workbook.getSheet(excelSheet.getSheetTitle()));
    }

    protected List<T> getContentRows() {
        return getRows()
                .stream()
                .skip(excelSheet.getHeaderRows())
                .map(rowToObjectConverter::convert)
                .collect(Collectors.toList());
    }
}
