package ru.vtb.opk.apitest.models;


import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@EqualsAndHashCode
@SuperBuilder
@NoArgsConstructor
@ToString
public class AppVersion {
    private String version;
    private String name;

}
