package core.helper.http;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Attachment {
    private final String field;
    private final String fileName;
    private byte[] bytes;
}
