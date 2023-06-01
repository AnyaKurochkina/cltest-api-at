package models.cloud.secretService;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Engine{
	private String updatedAt;
	private String segment;
	private String name;
	private String createdAt;
	private String id;
}