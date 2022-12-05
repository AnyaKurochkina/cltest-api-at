package models.cloud.portalBack;

import lombok.*;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class RolesItem{
	private String name;
	private String id;
}