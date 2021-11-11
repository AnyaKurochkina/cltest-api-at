package models.productCatalog.StringModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BindingsItem{

	@NonNull
	@JsonProperty("role")
	private String role;
}