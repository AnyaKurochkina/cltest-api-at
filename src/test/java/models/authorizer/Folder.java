package models.authorizer;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import models.Entity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
public class Folder extends Entity {
    public String name;
    public String kind;
    public String title;
    public String parentId;
    public List<String> informationSystemIds;

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
            title = "API " + new Timestamp(System.currentTimeMillis());
        }
        if (informationSystemIds == null) {
            InformationSystem informationSystem = InformationSystem.builder().build().createObject();
            informationSystemIds = new ArrayList<>();
            informationSystemIds.add(informationSystem.getId());
        }
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("/structure/create_folder.json")
                .set("$.folder.kind", kind)
                .set("$.folder.title", title)
                .set("$.folder.name", name)
                .set("$.folder.information_system_ids", informationSystemIds)
                .build();
    }

    public void edit(){
        String titleNew = new Http(Configure.ResourceManagerURL)
                .body(toJson())
                .patch("/v1/folders/{}", name)
                .assertStatus(200)
                .jsonPath()
                .getString("data.title");
        Assertions.assertEquals(title, titleNew, "Title папки не изменился");
        setTitle(titleNew);
    }

    @Override
    @Step("Создание папки")
    protected void create() {
        String url = kind.equals(BUSINESS_BLOCK) ? "/v1/organizations/vtb/folders" : String.format("/v1/folders/%s/folders", parentId);
        name = new Http(Configure.ResourceManagerURL)
                .body(toJson())
                .post(url)
                .assertStatus(201)
                .jsonPath()
                .getString("data.name");
    }

    @Override
    @Step("Удаление папки")
    protected void delete() {
        new Http(Configure.ResourceManagerURL)
                .delete("/v1/folders/" + name)
                .assertStatus(204);
    }
}
