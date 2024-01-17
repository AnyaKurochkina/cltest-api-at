package steps.t1.cdn;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.t1.cdn.GetSourceGroupList;
import models.t1.cdn.SourceGroup;

import java.util.List;

import static core.helper.Configure.cdnProxy;

public class CdnOriginGroupsClient extends AbstractCdnClient {

    private static final String BASE_PATH = apiUrl + "origin-groups/";

    @Step("Удаление группы источника")
    public static void deleteSourceGroupByName(String projectId, String name) {
        String id = getListSourceGroup(projectId).stream()
                .filter(x -> x.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError(String.format("Не найдено ниодной группы источника с именем: %s", name)))
                .getId();
        deleteSourceGroup(projectId, id);
    }

    @Step("Получения списка группы источника")
    public static List<SourceGroup> getListSourceGroup(String projectId) {
        return new Http(cdnProxy)
                .setRole(Role.CLOUD_ADMIN)
                .get(BASE_PATH, projectId)
                .assertStatus(200)
                .extractAs(GetSourceGroupList.class)
                .getList();
    }

    @Step("Удаление группы источника")
    private static Response deleteSourceGroup(String projectId, String id) {
        return getRequestSpec()
                .delete(BASE_PATH + id, projectId);
    }
}
