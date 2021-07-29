package core.helper;

import lombok.extern.log4j.Log4j2;
import models.orderService.interfaces.IProduct;

import java.nio.file.FileSystems;
import java.util.Random;

@Log4j2
public final class StringUtils {
    public static final String SPT = FileSystems.getDefault().getSeparator();
    public StringUtils() {
    }

    public static String concatPathToFile(String... names) {
        return concatPath(true, names);
    }

    public static String concatPath(String... names) {
        return concatPath(false, names);
    }

    private static String concatPath(boolean toFile, String... names) {
        StringBuilder result = new StringBuilder();
        for (String str : names) {
            result.append(str);
            if (!str.endsWith(SPT)) {
                result.append(SPT);
            }
        }
        if (toFile) {
            result.setLength(result.length() - SPT.length());
        }
        return result.toString();
    }

    public String getRandString(int len) {
        log.info("Генерация рандомной строки");
        Random random = new Random();
        String symb = "qwertyuiopasdfghjklzxcvbnm1234567890";
        String result = "";
        for (int i=0; i < len; ++i) {
            int position = random.nextInt(symb.length());
            result = result + symb.charAt(position);
        }
        return result;
    }

}
