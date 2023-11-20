package models.cloud.productCatalog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductAudit{

	@JsonProperty("audit_id")
	private String auditId;

	@JsonProperty("obj_id")
	private String objId;

	@JsonProperty("user__email")
	private String userEmail;

	@JsonProperty("create_dt")
	private String createDt;

	@JsonProperty("audit_dt")
	private String auditDt;

	@JsonProperty("change_type")
	private String changeType;

	@JsonProperty("version")
	private String version;

	@JsonProperty("obj_keys")
	private Object objKeys;
}