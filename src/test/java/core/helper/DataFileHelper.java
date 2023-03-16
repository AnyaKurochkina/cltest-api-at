package core.helper;


import lombok.SneakyThrows;

import java.io.BufferedWriter;
import java.io.IOException;
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
     */
    @SneakyThrows
    public static void write(String fileName, String text) {
        //Paths.get(fileName).toFile().getParentFile().mkdirs();
        Files.write(Paths.get(fileName), text.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    @SneakyThrows
    public static void delete(String fileName) {
       Files.delete(Paths.get(fileName));
    }

    /**
     * Добавление текста в конец файла
     *
     * @param fileName имя файла в корне проекта или полный путь к файлу + именем
     * @param text текст для добавления в файл
     */
    public static void appendToFile(String fileName, String text) {
        try {
            Files.createDirectories(Paths.get(fileName).getParent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName), StandardCharsets.UTF_8, StandardOpenOption.APPEND,StandardOpenOption.CREATE)) {
            writer.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Чтение файла в одну строку
     * @param fileName имя файла в корне проекта или полный путь к файлу + именем
     * @return переданный файл в виде строки
     */
    @SneakyThrows
    public static String read(String fileName) {
        StringBuilder sb = new StringBuilder();
        List<String> fileStr = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        fileStr.stream().map(str -> str + "\n").forEach(sb::append);
        return sb.toString().trim();
    }

    @SneakyThrows
    public static byte[] readBytes(String fileName) {
        return Files.readAllBytes(Paths.get(fileName));
    }

}