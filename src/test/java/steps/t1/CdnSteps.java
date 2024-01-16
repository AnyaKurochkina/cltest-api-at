package steps.t1;

import core.enums.Role;
import core.helper.http.Http;
import io.qameta.allure.Step;
import models.t1.cdn.GetSourceGroupList;
import models.t1.cdn.Resource;
import models.t1.cdn.SourceGroup;
import org.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

import static core.helper.Configure.cdnProxy;

public class CdnSteps {
    private static final String apiUrl = "/api/v1/";

    @Step("Удаление группы источника")
    public static void deleteSourceGroup(String projectId, String name) {
        List<SourceGroup> sourceGroupList = getListSourceGroup(projectId).stream().filter(x -> x.getName().equals(name))
                .collect(Collectors.toList());
        String id = sourceGroupList.get(0).getId();
        new Http(cdnProxy)
                .setRole(Role.CLOUD_ADMIN)
                .delete(apiUrl + "projects/{}/origin-groups/{}", projectId, id)
                .assertStatus(204);
    }

    @Step("Получения списка группы источника")
    public static List<SourceGroup> getListSourceGroup(String projectId) {
        return new Http(cdnProxy)
                .setRole(Role.CLOUD_ADMIN)
                .get(apiUrl + "projects/{}/origin-groups", projectId)
                .assertStatus(200)
                .extractAs(GetSourceGroupList.class)
                .getList();
    }

    @Step("Создание ресурса")
    public static Resource createResource(String projectId, JSONObject resource) {
        return new Http(cdnProxy)
                .setRole(Role.CLOUD_ADMIN)
                .body(resource)
                .post(apiUrl + "projects/{}/resources", projectId)
                .assertStatus(201)
                .extractAs(Resource.class);
    }
}
