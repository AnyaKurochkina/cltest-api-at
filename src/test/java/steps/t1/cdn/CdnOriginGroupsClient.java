package steps.t1.cdn;

import core.helper.http.Response;
import io.qameta.allure.Step;
import models.t1.cdn.SourceGroup;

import java.util.List;

public class CdnOriginGroupsClient extends AbstractCdnClient {

    private static final String BASE_PATH = API_URL + "origin-groups";

    @Step("Удаление группы источника")
    public static void deleteSourceGroupByName(String projectId, String name) {
        String id = getListSourceGroup(projectId).stream()
                .filter(x -> x.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError(String.format("Не найдено ни одной группы источника с именем: %s", name)))
                .getId();
        deleteSourceGroup(projectId, id);
    }

    @Step("Получения списка групп источника")
    public static List<SourceGroup> getListSourceGroup(String projectId) {
        return getRequestSpec()
                .get(BASE_PATH, projectId)
                .assertStatus(200)
                .jsonPath()
                .getList("list", SourceGroup.class);
    }

    @Step("Удаление группы источника")
    private static Response deleteSourceGroup(String projectId, String id) {
        return getRequestSpec()
                .delete(BASE_PATH + "/" + id, projectId);
    }
}
