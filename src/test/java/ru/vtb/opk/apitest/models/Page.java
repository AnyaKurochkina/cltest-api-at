package ru.vtb.opk.apitest.models;


import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@EqualsAndHashCode
@SuperBuilder
@NoArgsConstructor
@ToString
public class Page {
    private String size;
    private String totalElements;
    private String totalPages;
    private String number;
}
