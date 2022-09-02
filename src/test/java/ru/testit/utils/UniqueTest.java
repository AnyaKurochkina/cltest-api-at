package ru.testit.utils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.testit.model.request.Attachment;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@ToString(onlyExplicitlyIncluded = true)
public class UniqueTest {
    @EqualsAndHashCode.Include
    @ToString.Exclude
    String externalId;
    @EqualsAndHashCode.Include
    @ToString.Exclude
    String configurationId;
    @Setter
    boolean finished;

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
        if (attachments.get() == null)
            attachments.set(new ArrayList<>());
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
