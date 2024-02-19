package steps.t1.cdn;

import io.qameta.allure.Step;
import models.t1.cdn.SourceGroup;
import tests.routes.cdn.CdnOriginGroupsApi;

import java.util.List;

public class CdnOriginGroupsClient extends AbstractCdnClient {

    @Step("Получения списка групп источника")
    public static List<SourceGroup> getListSourceGroup(String projectId) {
        return getRequestSpec().api(CdnOriginGroupsApi.getSourceGroups, projectId)
                .jsonPath()
                .getList("list", SourceGroup.class);
    }

    @Step("Удаление группы источника по имени: {1}")
    public static void deleteSourceGroupByName(String projectId, String name) {
        String id = getListSourceGroup(projectId).stream()
                .filter(x -> x.getName().contains(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError(String.format("Не найдено ни одной группы источника с именем: %s", name)))
                .getId();

        deleteSourceGroup(projectId, id);
    }

    @Step("Удаление группы источника")
    private static void deleteSourceGroup(String projectId, String id) {
        getRequestSpec().api(CdnOriginGroupsApi.deleteSourceGroupById, projectId, id);
    }
}
