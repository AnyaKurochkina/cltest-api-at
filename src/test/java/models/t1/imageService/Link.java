package models.t1.imageService;

import lombok.*;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class Link {
    private String id;
    private String title;
    private String link;
    private String value;
}
