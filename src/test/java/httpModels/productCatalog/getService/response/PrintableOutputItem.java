package httpModels.productCatalog.getService.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

public class PrintableOutputItem{

	@JsonProperty("node_name")
	private String nodeName;

	@JsonProperty("type")
	private String type;

	@JsonProperty("data_path")
	private String dataPath;
}