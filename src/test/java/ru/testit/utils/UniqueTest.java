package ru.testit.utils;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.testit.services.LinkItem;

import java.util.LinkedList;
import java.util.StringJoiner;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
public class UniqueTest {
    String externalId;
    String configurationId;

    private static final ThreadLocal<StringJoiner> stepLog = new ThreadLocal<>();

    public static void writeStepLog(String text){
        if(stepLog.get() != null)
            stepLog.get().add(text);
    }

    public static void clearStepLog(){
        stepLog.remove();
        stepLog.set(new StringJoiner("\n"));
    }

    public static String getStepLog(){
        if(stepLog.get() == null)
            return null;
        return stepLog.get().toString();
    }
}
