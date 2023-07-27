package api.cloud.secretService.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AccessRuleResponse {
	private Date createdAt;
	private String email;
	private String id;
	private Boolean locked;
	private String mode;
	private Boolean publicAccess;
	private String secretId;
	private Date updatedAt;
	private String username;
}