package ui.t1.pages.S3Storage.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccessBucketLevel {

    OWNER_ONLY("Доступ только у владельца"),
    READ_FOR_ALL("Чтение для всех пользователей"),
    READ_AND_WRITE_FOR_ALL("Запись и чтение для всех пользователей"),
    READ_FOR_AUTH("Чтение для аутентифицированных пользователей");

    private final String value;
}
