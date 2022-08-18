package models.feedService.tag;

import core.enums.Role;
import core.helper.JsonHelper;
import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;
import steps.feedService.FeedServiceSteps;

import static core.helper.Configure.FeedServiceURL;

@Log4j2
@Getter
@EqualsAndHashCode(exclude = {"jsonTemplate", "feedService"})
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"jsonTemplate", "feedService"})
public class FeedTag extends Entity {
	private String key;
	private Integer id;
	private Integer parent;
	private String title;
	@Getter(AccessLevel.NONE)
	private String jsonTemplate;
	@Getter(AccessLevel.NONE)
	private String feedService;

	@Builder
	public FeedTag(Integer parent, String title, String key) {
		this.parent = parent;
		this.title = title;
		this.key = key;
	}

	@Override
	public Entity init() {
		feedService = "/api/v1/events-feed/tags/";
		jsonTemplate = "feedService/createTag.json";
		return this;
	}

	@Override
	public JSONObject toJson() {
		return JsonHelper.getJsonTemplate(jsonTemplate)
				.set("$.title", title)
				.set("$.key", key)
				.set("$.parent", parent)
				.build();
	}

	@Override
	@Step("Создание Tag")
	protected void create() {
		FeedTag feedByName = FeedServiceSteps.getFeedTagByName(title);
		if (feedByName != null) {
			FeedServiceSteps.deleteTag(feedByName.getId());
		}
		id = new Http(FeedServiceURL)
				.setRole(Role.PRODUCT_CATALOG_ADMIN)
				.body(toJson())
				.post(feedService)
				.assertStatus(201)
				.extractAs(FeedTag.class)
				.getId();
	}

	@Override
	@Step("Удаление Tag")
	protected void delete() {
		new Http(FeedServiceURL)
				.setRole(Role.PRODUCT_CATALOG_ADMIN)
				.delete(feedService + id + "/")
				.assertStatus(204);
	}
}