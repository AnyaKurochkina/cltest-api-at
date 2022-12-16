package ui.cloud.pages;

import lombok.Builder;

@Builder
public class AclTransaction {
    String certificate;
    String mask;
    Type type;

    enum Type {
        BY_MASK,
        BY_NAME,
        ALL_TRANSACTION;
    }
}
