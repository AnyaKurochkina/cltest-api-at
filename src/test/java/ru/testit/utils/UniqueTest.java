package ru.testit.utils;

import lombok.*;
import ru.testit.model.request.Attachment;
import ru.testit.services.LinkItem;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

@EqualsAndHashCode
@Getter
@ToString
public class UniqueTest {
    String externalId;
    String configurationId;

    public UniqueTest(String externalId, String configurationId) {
        this.externalId = externalId;
        this.configurationId = configurationId;
    }

    private static final InheritableThreadLocal<StringJoiner> stepLog = new InheritableThreadLocal<>();
    private static final InheritableThreadLocal<List<Attachment>> attachments = new InheritableThreadLocal<>();

    public static void addAttachment(Attachment attachment) {
        if (attachments.get() == null)
            attachments.set(new ArrayList<>());
        attachments.get().add(attachment);
    }

    public static List<Attachment> getAndClearAttachmentList() {
        List<Attachment> list = new ArrayList<>(attachments.get());
        attachments.remove();
        return list;
    }

    public static void writeStepLog(String text) {
        if (stepLog.get() != null)
            stepLog.get().add(text);
    }

    public static void clearStepLog() {
        stepLog.remove();
        stepLog.set(new StringJoiner("\n"));
    }

    public static String getStepLog() {
        if (stepLog.get() == null)
            return null;
        return stepLog.get().toString();
    }
}
