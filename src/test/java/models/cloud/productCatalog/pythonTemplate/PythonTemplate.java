package models.cloud.productCatalog.pythonTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.productCatalog.template.Template;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static core.helper.StringUtils.getRandomStringApi;
import static steps.productCatalog.PythonTemplateSteps.*;
import static steps.productCatalog.TemplateSteps.createTemplateByName;

@Log4j2
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, exclude = {"versionCreateDt", "versionChangedByUser", "updateDt", "createDt", "versionList"})
@ToString
public class PythonTemplate extends Entity {

	@JsonProperty("python_code")
	private String pythonCode;
	@JsonProperty("version_list")
	private List<String> versionList;
	@JsonProperty("version_create_dt")
	private String versionCreateDt;
	@JsonProperty("current_version")
	private String currentVersion;
	@JsonProperty("description")
	private String description;
	@JsonProperty("title")
	private String title;
	@JsonProperty("version")
	private String version;
	@JsonProperty("last_version")
	private String lastVersion;
	@JsonProperty("object_info")
	private String objectInfo;
	@JsonProperty("version_changed_by_user")
	private String versionChangedByUser;
	@JsonProperty("update_dt")
	private String updateDt;
	@JsonProperty("name")
	private String name;
	@JsonProperty("create_dt")
	private String createDt;
	@JsonProperty("id")
	private String id;
	@JsonProperty("template_id")
	private Integer templateId;
	@JsonProperty("template_version")
	private String templateVersion;
	@JsonProperty("template_version_pattern")
	private String templateVersionPattern;
	@JsonProperty("python_data")
	private Object pythonData;

	@Override
	public Entity init() {
		if (templateId == null) {
			Template template = createTemplateByName(getRandomStringApi(6));
			templateId = template.getId();
			templateVersion = "";
			templateVersionPattern = "";
		}
		return this;
	}

	@Override
	public JSONObject toJson() {
		return JsonHelper.getJsonTemplate("productCatalog/pythonTemplate/createPythonTemplate.json")
				.set("$.python_code", pythonCode)
				.set("$.name", name)
				.set("$.title", title)
				.set("$.current_version", currentVersion)
				.set("$.description", description)
				.set("$.version", version)
				.set("$.create_dt", createDt)
				.set("$.update_dt", updateDt)
				.set("$.version_list", versionList)
				.set("$.version_create_dt", versionCreateDt)
				.set("$.last_version", lastVersion)
				.set("$.object_info", objectInfo)
				.set("$.version_changed_by_user", versionChangedByUser)
				.set("$.python_data", pythonData)
				.set("$.template_id", templateId)
				.set("$.template_version", templateVersion)
				.set("$.template_version_pattern", templateVersionPattern)
				.build();
	}

	@Override
	protected void create() {
		if (isPythonTemplateExists(name)) {
			deletePythonTemplateByName(name);
		}
		PythonTemplate pythonTemplate = createPythonTemplate(toJson());
		id = pythonTemplate.getId();
		versionList = pythonTemplate.getVersionList();
		lastVersion = pythonTemplate.getLastVersion();
		Assertions.assertNotNull(id, "Продукт с именем: " + name + ", не создался");
	}

	@Override
	protected void delete() {
		deletePythonTemplate(id);
	}
}