package clp.core.iso;



import clp.core.exception.CustomException;
import clp.core.iso.core.parse.FieldsMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс создания сообщения по стандарту ISO 8583.
 */
public class UniversalRequestBuilder {
    private static final String DEFAULT_HEADER;
    private static final Map<String, String> DEFAULT_FIELDS;
    private static final String TEMPLATE_FIELD_PATTERN;
    private final UniversalRequest request;

    static {
        DEFAULT_HEADER = "A4M08000";
        DEFAULT_FIELDS = new HashMap<String, String>() {
            {
                //TODO: переделать через FieldsMap.getISOField(String);
                final Date date = new Date();
                final String supposedlyRandomNumber = new StringBuilder(String.valueOf(date.getTime())).reverse().toString().substring(0, 5);
                this.put("11", supposedlyRandomNumber);
                this.put("37", supposedlyRandomNumber);
                this.put("108", supposedlyRandomNumber);

                final SimpleDateFormat sdf_full = new SimpleDateFormat("MMddHHmmss");
                sdf_full.setTimeZone(TimeZone.getTimeZone("UTC"));
                this.put("7", sdf_full.format(date));

                this.put("12", new SimpleDateFormat("HHmmss").format(date));
                this.put("49", "643");
                this.put("13", new SimpleDateFormat("MMdd").format(date));
                this.put("22", "901");
                this.put("32", "00001");
                this.put("43.9", new SimpleDateFormat("yyyyMMdd").format(date));
                this.put("43.11", "VB24");
                this.put("18", "6011");
                this.put("43.1", "DEVICE IN MOSCOW");
                this.put("43.12", "DEVICE IN MOSCOW");
            }
        };
        TEMPLATE_FIELD_PATTERN = "<field name=\"(.*)\">(.*)</field>";
    }

    private UniversalRequestBuilder() {
        this.request = new UniversalRequest();
    }

    /**
     * Создание строителя с сообщением по умолчанию.
     * @return Строитель сообщения.
     */
    public static UniversalRequestBuilder createDefaultBuilder() {
        return UniversalRequestBuilder.createEmptyBuilder().setDefaultHeader().setDefaultFields();
    }

    /**
     * Создание строителя с пустым сообщением.
     * @return Строитель сообщения.
     */
    public static UniversalRequestBuilder createEmptyBuilder() {
        return new UniversalRequestBuilder();
    }

    /**
     * Создание строителя с сообщением с учётом данных из файла.
     * @param templateFile Файл с данными.
     * @param setDefaultFields Необходимость задания заголовка и полей по умолчанию.
     * @return Строитель сообщения.

     */
    public static UniversalRequestBuilder createBuilderFromTemplate(final File templateFile, final boolean setDefaultFields) throws CustomException {
        final UniversalRequestBuilder builder = UniversalRequestBuilder.createEmptyBuilder();

        // Чтение файла.
        List<String> content = Collections.emptyList();
        try {
            //TODO: переделать чтение с использованием FileUtils.
            content = Files.readAllLines(templateFile.toPath());
        } catch (final IOException cause) {
            throw new CustomException("Ошибка при чтении файла-шаблона для ISO-запроса.");
        }

        for (final String line: content) {
            final Matcher matcher = Pattern.compile(UniversalRequestBuilder.TEMPLATE_FIELD_PATTERN).matcher(line);
            if (matcher.matches()) {
                // Запись значений в карту.
                builder.setField(matcher.group(1), matcher.group(2));
            }
        }

        // Установка значений по умолчанию.
        if (setDefaultFields) {
            builder.setDefaultHeader();
            builder.setDefaultFields();
        }

        return builder;
    }

    /**
     * Создание строителя с сообщением с учётом данных из файла.
     * @param templateFile Файл с данными.
     * @return Строитель сообщения.

     */
    public static UniversalRequestBuilder createBuilderFromTemplate(final File templateFile) throws CustomException {
        return UniversalRequestBuilder.createBuilderFromTemplate(templateFile, true);
    }

    /**
     * Создание строителя на основе сообщения.
     * @param request Сообщение, на основе которого создаётся строитель.
     * @return Строитель сообщения.
     */
    public static UniversalRequestBuilder createBuilderFromRequest(final UniversalRequest request) {
        return UniversalRequestBuilder.createEmptyBuilder().setHeader(request.getISOHeader()).setFields(request.getFields().entrySet());
    }

    /**
     * Создание сообщения.
     * @return Сообщение по стандарту ISO 8583.
     */
    public UniversalRequest build() {
        return this.request;
    }

    /**
     * Заполнение поля сообщения.
     * @param name Имя или код поля.
     * @param value Значение поля.
     * @return Строитель сообщения.
     */
    public UniversalRequestBuilder setField(final String name, final String value) {
        //TODO: добавить определение поля MTI и заголовка.
        if (Pattern.compile("\\d+").matcher(name).replaceAll("").equals("")) {
            return this.setField(Integer.valueOf(name), value);
        } else if (Pattern.compile("\\d+\\.\\d+").matcher(name).replaceAll("").equals("")) {
            return this.setField(Float.valueOf(name), value);
        } else {
            this.request.setField(FieldsMap.getISOField(name), value);
            return this;
        }
    }

    /**
     * Заполнение полей сообщения.
     * @param entries Набор ключей и значений.
     * @return Строитель соощения.
     */
    public UniversalRequestBuilder setFields(final Set<Entry<String, String>> entries) {
        for (final Entry<String, String> entry: entries) {
            this.setField(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * Заполнение поля сообщения.
     * @param id Код поля.
     * @param value Значение поля.
     * @return Строитель сообщения.
     */
    public UniversalRequestBuilder setField(final int id, final String value) {
        this.request.setField(String.valueOf(id), value);
        return this;
    }

    /**
     * Заполнение поля сообщения.
     * @param id Код поля.
     * @param value Значение поля.
     * @return Строитель сообщения.
     */
    public UniversalRequestBuilder setField(final float id, final String value) {
        this.request.setField(String.valueOf(id), value);
        return this;
    }

    /**
     * Заполнение заголовка сообщения.
     * @param value Значение поля.
     * @return Строитель сообщения.
     */
    //TODO: переделать через стандартные '.setField(String, String)'.
    public UniversalRequestBuilder setHeader(final String value) {
        this.request.setISOHeader(value);
        return this;
    }

    private UniversalRequestBuilder setDefaultFields() {
        return this.setFields(UniversalRequestBuilder.DEFAULT_FIELDS.entrySet());
    }

    private UniversalRequestBuilder setDefaultHeader() {
        return this.setHeader(UniversalRequestBuilder.DEFAULT_HEADER);
    }
}
