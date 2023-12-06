package models.t1.cdn;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import lombok.*;
import org.json.JSONObject;

import java.util.List;
@Builder
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Resource{
	private String hostHeader;
	private String website;
	private String certType;
	private Boolean active;
	private Object sslData;
	private List<String> hostnames;
	private String originProtocol;
	private Object originGroup;
	private String sourceType;
	private String domainName;
	@JsonProperty("request_id")
	private String requestId;
	private String hostType;
	@JsonProperty("redirect_https_to_http")
	private Boolean redirectHttpsToHttp;
	@JsonProperty("redirect_http_to_https")
	private Boolean redirectHttpToHttps;

	public Resource (String domainName, List<String> hostnames) {
		this.domainName=domainName;
		this.hostnames = hostnames;
	}

	public JSONObject toJson() {
		return JsonHelper.getJsonTemplate("t1/cdn/createCdn.json")
				.set("$.hostHeader", hostHeader)
				.set("$.website", website)
				.set("$.certType", certType)
				.set("$.active", active)
				.set("$.sslData", sslData)
				.set("$.request_id", requestId)
				.set("$.hostnames", hostnames)
				.set("$.originProtocol", originProtocol)
				.set("$.originGroup", originGroup)
				.set("$.sourceType", sourceType)
				.set("$.domainName", domainName)
				.set("$.hostType", hostType)
				.set("$.redirectHttpsToHttp", redirectHttpsToHttp)
				.set("$.redirectHttpToHttps", redirectHttpToHttps)
				.build();
	}
}