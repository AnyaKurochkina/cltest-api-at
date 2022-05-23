package ru.testit.utils;

import lombok.*;
import ru.testit.services.LinkItem;

import java.util.LinkedList;
import java.util.StringJoiner;

@EqualsAndHashCode
@Getter
@ToString
public class UniqueTest {
    String externalId;
    String configurationId;
    @Setter
    StepNode step;

    public UniqueTest(String externalId, String configurationId) {
        this.externalId = externalId;
        this.configurationId = configurationId;
    }

    private static final InheritableThreadLocal<StringJoiner> stepLog = new InheritableThreadLocal<>();

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
