package ru.testit.model.request;

import lombok.Data;

@Data
public class Attachment {
    String fileName;
    byte[] bytes;
    String id;
}
