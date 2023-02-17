package models.cloud.productCatalog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InformationSystem{

	@JsonProperty("critical_category")
	private List<String> criticalCategory;

	@JsonProperty("id")
	private List<String> id;
}