package models.productCatalog.icon;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import static core.helper.Configure.ProductCatalogURL;
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
	private String jsonTemplate;
	private String productName;

	@Override
	public Entity init() {
		jsonTemplate = "productCatalog/examples/createExample.json";
		productName = "/api/v1/example/";
		return this;
	}

	@Override
	public JSONObject toJson() {
		return JsonHelper.getJsonTemplate(jsonTemplate)
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
	@Step("Создание Иконки")
	protected void create() {
		if (isIconExists(name)) {
			deleteIconByName(name);
		}
		Icon icon = new Http(ProductCatalogURL)
				.body(toJson())
				.post(productName)
				.assertStatus(201)
				.extractAs(Icon.class);
		id = icon.getId();
		updateDt = icon.getUpdateDt();
		createDt = icon.getCreateDt();
		Assertions.assertNotNull(id, "Пример с именем: " + name + ", не создался");
	}


	@Override
	protected void delete() {
		deleteIconById(id);
	}
}