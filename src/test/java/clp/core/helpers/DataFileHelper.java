package clp.core.helpers;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class DataFileHelper {

    /**
     * Запись текста в файл
     * @param fileName имя файла в корне проекта или полный путь к файлу + именем
     * @param text текст который нужен для записи в файл
     * @throws IOException - в случае ошибок ввода вывода
     */
    public static void write(String fileName, String text) throws IOException {
        Paths.get(fileName).toFile().getParentFile().mkdirs();
        Files.deleteIfExists(Paths.get(fileName));
        Files.write(Paths.get(fileName), text.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    /**
     * Добавление текста в конец файла
     *
     * @param fileName имя файла в корне проекта или полный путь к файлу + именем
     * @param newText текст для добавления в файл
     * @throws IOException - в случае ошибок ввода вывода
     */
    public static void appendToFile(String fileName, String newText) throws IOException {
        Files.write(Paths.get(fileName), newText.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
    }

    /**
     * Чтение файла в одну строку
     * @param fileName имя файла в корне проекта или полный путь к файлу + именем
     * @return переданный файл в виде строки
     * @throws IOException - в случае ошибок ввода вывода
     */
    public static String read(String fileName) throws IOException {
        StringBuilder sb = new StringBuilder();
        List<String> fileStr = Files.readAllLines(Paths.get(fileName), Charset.forName("UTF-8"));
        fileStr.stream().map(str -> str + "\n").forEach(sb::append);
        return sb.toString();
    }

}
