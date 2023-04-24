package models.t1.imageService;

import lombok.*;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Categories {

    private String id;
    private String name;
}
