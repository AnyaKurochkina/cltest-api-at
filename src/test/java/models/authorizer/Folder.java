package models.authorizer;

import core.helper.Configure;
import core.helper.Http;
import core.random.string.RandomStringGenerator;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import models.Entity;
import org.json.JSONObject;
import steps.authorizer.AuthorizerSteps;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class Folder extends Entity {
    public String name;
    public String kind;
    public String title;
    public String parentId;
    public List<String> informationSystemIds;

    @Builder.Default
    transient AuthorizerSteps authorizerSteps = new AuthorizerSteps();
    transient static final public String BUSINESS_BLOCK = "business_block";
    transient static final public String DEPARTMENT = "department";
    transient static final public String DEFAULT = "default";

    @Override
    public Entity init() {
        if (parentId == null) {
            switch (kind) {
                case BUSINESS_BLOCK:
                    Organization organization = Organization.builder().build().createObject();
                    parentId = organization.getName();
                    break;
                case DEPARTMENT: {
                    Folder folder = Folder.builder().kind(BUSINESS_BLOCK).build().createObject();
                    parentId = folder.getName();
                    break;
                }
                case DEFAULT: {
                    Folder folder = Folder.builder().kind(DEPARTMENT).build().createObject();
                    parentId = folder.getName();
                    break;
                }
            }
        }
        if (title == null) {
            title = new RandomStringGenerator().generateByRegex("FOLDER .{1,20}");
        }
        if (informationSystemIds == null) {
            InformationSystem informationSystem = InformationSystem.builder().build().createObject();
            informationSystemIds = new ArrayList<>();
            informationSystemIds.add(informationSystem.getId());
        }
        return this;
    }

    //    @Override
    public JSONObject toJson() {
        return jsonHelper.getJsonTemplate("/structure/create_folder.json")
                .set("$.folder.kind", kind)
                .set("$.folder.title", title)
                .set("$.folder.name", name)
                .set("$.folder.information_system_ids", informationSystemIds)
                .build();
    }

    @Override
    @Step("Создание папки")
    protected void create() {
        String url = kind.equals(BUSINESS_BLOCK) ? "organizations/vtb/folders" : String.format("folders/%s/folders", parentId);
        name = new Http(Configure.AuthorizerURL)
                .body(toJson())
                .post(url)
                .assertStatus(201)
                .jsonPath()
                .getString("data.name");
    }

    @Override
    @Step("Удаление папки")
    protected void delete() {
        new Http(Configure.AuthorizerURL)
                .delete("folders/" + name)
                .assertStatus(204);
    }
}
