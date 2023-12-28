package core.excel.excel_core.field;


import org.junit.jupiter.api.Assertions;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractExcelFields<T> {

    public abstract Map<Integer, ExcelField<T>> getHeaderFieldsMappings();

    protected Map<Integer, ExcelField<T>> createFieldsMappings(Set<? extends ExcelField<T>> fields) {
        return createFieldsMappings(fields, 0);
    }

    protected Map<Integer, ExcelField<T>> createFieldsMappings(Set<? extends ExcelField<T>> fields, int offset) {
        Map<Integer, ExcelField<T>> headerFieldsMappings = new HashMap<>();

        List<String> allNextFields = fields.stream().map(ExcelField::getNextFieldName).filter(Objects::nonNull).collect(Collectors.toList());
        List<String> firstFields = fields.stream().map(ExcelField::getFieldName).filter(Objects::nonNull).collect(Collectors.toList());
        firstFields.removeAll(allNextFields);

        Assertions.assertEquals(firstFields.size(), 1, "В авто-тестах ошибка, найдено несколько первых ячеек в шаблоне/не найдено ячеек вовсе");
        ExcelField<T> firstExcelField = fields.stream()
                .filter(f -> f.getFieldName().equals(firstFields.get(0)))
                .findFirst().orElseThrow(() ->
                        new RuntimeException("Ошибка при получении первого поля excel файла"));

        if (fields.size() == 1) {
            headerFieldsMappings.put(offset, firstExcelField);
            return headerFieldsMappings;
        }

        List<ExcelField<T>> possibleLastExcelFields = fields.stream().filter(f -> f.getNextFieldName() == null).collect(Collectors.toList());
        Assertions.assertEquals(possibleLastExcelFields.size(), 1, "В авто-тестах ошибка, найдено несколько последних ячеек в шаблоне/не найдено ячеек вовсе");

        headerFieldsMappings.put(offset, firstExcelField);

        ExcelField<T> lastExcelField = possibleLastExcelFields.get(0);
        headerFieldsMappings.put(fields.size() - 1 + offset, lastExcelField);

        ExcelField<T> lastProcessedExcelField = firstExcelField;
        for (int i = 1 + offset; i <= fields.size() - 2 + offset; i++) {
            ExcelField<T> finalLastExcelField = lastProcessedExcelField;
            List<ExcelField<T>> nextExcelFields = fields.stream()
                    .filter(field -> finalLastExcelField.getNextFieldName().equals(field.getFieldName()))
                    .collect(Collectors.toList());

            Assertions.assertEquals(nextExcelFields.size(), 1, "В авто-тестах ошибка, найдено несколько ячеек в шаблоне/не найдено ячеек вовсе. " +
                    "Ячейка: %s");

            lastProcessedExcelField = nextExcelFields.get(0);
            headerFieldsMappings.put(i, lastProcessedExcelField);
        }
        return headerFieldsMappings;
    }
}
