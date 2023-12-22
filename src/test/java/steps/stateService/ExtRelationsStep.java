package steps.stateService;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import models.cloud.stateService.extRelations.ExtRelation;
import models.cloud.stateService.extRelations.GetExtRelationList;
import org.json.JSONObject;
import steps.Steps;

import java.util.List;

import static core.helper.Configure.stateServiceURL;

@Log4j2
public class ExtRelationsStep extends Steps {

    @Step("Создание ExtRelation")
    public static ExtRelation createExtRelation(String contextType, String contextId, String primaryItemId, String secondaryItemId,
                                                boolean isExclusive) {
        JSONObject jsonObject = ExtRelation.builder()
                .primaryItemId(primaryItemId)
                .secondaryItemId(secondaryItemId)
                .isExclusive(isExclusive)
                .build().toJson();
        return new Http(stateServiceURL)
                .withServiceToken()
                .body(jsonObject)
                .post("/api/v1/{}/{}/ext_relations/", contextType, contextId)
                .assertStatus(201)
                .extractAs(ExtRelation.class);
    }

    @Step("Создание ExtRelation")
    public static Response createExtRelationResponse(String contextType, String contextId, String primaryItemId, String secondaryItemId,
                                                     boolean isExclusive) {
        JSONObject jsonObject = ExtRelation.builder()
                .primaryItemId(primaryItemId)
                .secondaryItemId(secondaryItemId)
                .isExclusive(isExclusive)
                .build().toJson();
        return new Http(stateServiceURL)
                .withServiceToken()
                .body(jsonObject)
                .post("/api/v1/{}/{}/ext_relations/", contextType, contextId);
    }

    @Step("Удаление ExtRelation")
    public static void deleteExtRelation(String contextType, String contextId, Integer id) {
        new Http(stateServiceURL)
                .withServiceToken()
                .delete("/api/v1/{}/{}/ext_relations/{}/", contextType, contextId, id)
                .assertStatus(204);
    }

    @Step("Получение ExtRelation по id {id}")
    public static ExtRelation getExtRelationById(String contextType, String contextId, Integer id) {
        return new Http(stateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/{}/{}/ext_relations/{}/", contextType, contextId, id)
                .assertStatus(200)
                .extractAs(ExtRelation.class);
    }

    @Step("Проверка существования Relation по id")
    public static boolean isRelationExistById(String contextType, String contextId, Integer id) {
        List<ExtRelation> list = new Http(stateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/{}/{}/ext_relations/", contextType, contextId, id)
                .assertStatus(200)
                .extractAs(GetExtRelationList.class)
                .getList();
        return list.stream().anyMatch(x -> x.getId().equals(id));
    }
}
