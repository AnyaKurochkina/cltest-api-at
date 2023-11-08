package api.cloud.defectolog.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Defect {
	private Date createdAt;
	private Boolean solved;
	private Integer id;
	private Date solvedAt;
	private Group group;

	private List<DefectPages> defectPages;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Group{
		private String internalName;
		private String itemType;
		private String importance;
		private String name;
		private Integer id;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class DefectPages{
		private Integer id;
		private Integer pageNumber;
		private Integer patientsCount;
		private Integer healthyCount;
	}
}