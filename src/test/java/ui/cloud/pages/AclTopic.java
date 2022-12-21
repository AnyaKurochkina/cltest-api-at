package ui.cloud.pages;

import lombok.Builder;

@Builder
public class AclTopic {
    String certificate;
    String mask;
    Type type;

    public enum Type {
        BY_MASK,
        BY_NAME,
        ALL_TOPIC;
    }
}
