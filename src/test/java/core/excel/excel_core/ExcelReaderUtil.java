package core.excel.excel_core;

import io.qameta.allure.Step;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExcelReaderUtil {

    @Step("Чтение excel файла")
    public static Workbook getWorkbook(File file) {
        try {
            return WorkbookFactory.create(file);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed to read excel file: %s", e.getMessage()), e);
        }
    }

    public static List<Row> getRows(Sheet sheet) {
        if (sheet == null) {
            return Collections.emptyList();
        }

        Iterator<Row> iter = sheet.rowIterator();
        if (!iter.hasNext()) {
            return Collections.emptyList();
        }

        return IteratorUtils.toList(iter);
    }

    public static String readStringCell(Row row, Integer columnId) {
        if (row.getCell(columnId) == null) return "";

        return row.getCell(columnId).toString();
    }
}
