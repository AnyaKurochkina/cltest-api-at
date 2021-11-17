package core.helper;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public final class StringUtils {

    public String getRandString(int len) {
        log.info("Генерация рандомной строки");
        Random random = new Random();
        String symb = "qwertyuiopasdfghjklzxcvbnm1234567890";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < len; ++i) {
            int position = random.nextInt(symb.length());
            result.append(symb.charAt(position));
        }
        return result.toString();
    }

    public static String findByRegex(String regex, String text) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        if (matcher.find())
            return matcher.group(1);
        return null;
    }

    @SneakyThrows
    public static void copyAvailableFields(Object source, Object target) {
        Field[] fields = source.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())
                    && !Modifier.isFinal(field.getModifiers())) {
                field.setAccessible(true);
                field.set(target, field.get(source));
            }
        }
    }

}
