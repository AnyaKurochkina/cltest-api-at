package models.cloud.tarifficator;

import core.helper.JsonHelper;
import lombok.Data;
import lombok.SneakyThrows;
import org.json.JSONObject;

import java.util.List;

@Data
public class TariffClass {
	private Double amount;
	private String tariffPlanId;
	private CalculationEntity calculationEntity;
	private String kind;
	private String resourceType;
	private String tariffPlanServiceId;
	private String title;
	private String script;
	private String priceUnit;
	private Float price;
	private String calculationEntityFieldName;
	private String name;
	private String id;
	private String unit;
	private List<String> itemStates;
	private CalculateAttrs calculateAttrs;
	private Boolean discount;
	private Object tag;

	@SneakyThrows
	public JSONObject toJson() {
		return new JSONObject("{\"tariff_class\":" + JsonHelper.getCustomObjectMapper().writeValueAsString(this) + "}");
	}
}