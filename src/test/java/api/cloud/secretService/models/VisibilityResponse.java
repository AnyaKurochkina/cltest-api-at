package api.cloud.secretService.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@EqualsAndHashCode
public class VisibilityResponse {
	private Date availableTillDt;
	private Object cronSchedule;
	private Date updatedAt;
	private Boolean unrestricted;
	private String evaluationMode;
	private VisibilityCondition excludeVisibilityConditions;
	private VisibilityCondition visibilityConditions;
	private Date createdAt;
	private Date availableFromDt;
	private String id;
}