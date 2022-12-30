package ui.cloud.pages;

import lombok.Builder;

@Builder
public class Acl {
    String certificate;
    String mask;
    Type type;

    public enum Type {
        BY_MASK,
        BY_NAME,
        ALL_TOPIC,
        ALL_TRANSACTION;
    }
}
