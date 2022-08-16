package core.helper;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.intellij.lang.annotations.Language;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public final class StringUtils {

    public static String findByRegex(@Language("regexp") String regex, String text) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        if (matcher.find())
            return matcher.group(1);
        return null;
    }

    public static String format(String str, Object ... args){
        for (Object arg : args)
            str = str.replaceFirst("\\{}", Objects.requireNonNull(arg).toString());
        return str;
    }

    public static SelenideElement $x(@Language("XPath") String xpath, Object ... args) {
        return Selenide.$x(format(xpath, args));
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
    public static String getStackTraceThrowable(Throwable e){
        StringJoiner stack = new StringJoiner("\n\t");
        stack.add(e.toString());
        for (StackTraceElement s : e.getStackTrace()) {
            String str = s.toString();
            stack.add(str);
            if(str.startsWith("tests."))
                break;
        }
        return stack.toString();
    }

    public static String getStackTrace(StackTraceElement[] trace){
        StringJoiner stack = new StringJoiner("\n\t");
        for (StackTraceElement s : trace) {
            String e = s.toString();
            stack.add(e);
            if(e.startsWith("tests."))
                break;
        }
        return stack.toString();
    }

}
