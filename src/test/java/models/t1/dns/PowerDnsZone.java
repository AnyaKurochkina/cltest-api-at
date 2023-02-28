package models.t1.dns;

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
public class PowerDnsZone {

	@JsonProperty("api_rectify")
	private boolean apiRectify;

	@JsonProperty("dnssec")
	private boolean dnssec;

	@JsonProperty("kind")
	private String kind;

	@JsonProperty("notified_serial")
	private int notifiedSerial;

	@JsonProperty("masters")
	private List<Object> masters;

	@JsonProperty("slave_tsig_key_ids")
	private List<Object> slaveTsigKeyIds;

	@JsonProperty("soa_edit_api")
	private String soaEditApi;

	@JsonProperty("url")
	private String url;

	@JsonProperty("soa_edit")
	private String soaEdit;

	@JsonProperty("rrsets")
	private List<PowerDnsRrset> rrsets;

	@JsonProperty("serial")
	private int serial;

	@JsonProperty("nsec3param")
	private String nsec3param;

	@JsonProperty("edited_serial")
	private int editedSerial;

	@JsonProperty("name")
	private String name;

	@JsonProperty("master_tsig_key_ids")
	private List<Object> masterTsigKeyIds;

	@JsonProperty("id")
	private String id;

	@JsonProperty("last_check")
	private int lastCheck;

	@JsonProperty("nsec3narrow")
	private boolean nsec3narrow;

	@JsonProperty("account")
	private String account;
}