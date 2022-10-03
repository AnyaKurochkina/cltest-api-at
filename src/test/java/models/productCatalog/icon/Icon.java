package models.productCatalog.icon;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import static steps.productCatalog.IconSteps.*;

@Log4j2
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"jsonTemplate", "productName"}, callSuper = false)
@ToString(exclude = {"jsonTemplate", "productName"})
public class Icon extends Entity {
	private String id;
	private String image;
	private String name;
	private String description;
	@JsonProperty("image_hash")
	private String imageHash;
	private String title;
	private String url;
	@JsonProperty("update_dt")
	private String updateDt;
	@JsonProperty("create_dt")
	private String createDt;
	private String productName;

	@Override
	public Entity init() {
		productName = "/api/v1/icons/";
		return this;
	}

	@Override
	public JSONObject toJson() {
		return JsonHelper.getJsonTemplate("productCatalog/icon/createIcon.json")
				.set("$.name", name)
				.set("$.title", title)
				.set("$.description", description)
				.set("$.create_dt", createDt)
				.set("$.update_dt", updateDt)
				.set("$.image_hash", imageHash)
				.set("$.url", url)
				.set("$.image", image)
				.build();
	}

	@Override
	protected void create() {
		if (isIconExists(name)) {
			deleteIconByName(name);
		}
		Icon icon = createIcon(toJson());
		StringUtils.copyAvailableFields(icon, this);
		Assertions.assertNotNull(id, "Иконка с именем: " + name + ", не создалась");
	}

	@Override
	protected void delete() {
		deleteIconById(id);
	}
}