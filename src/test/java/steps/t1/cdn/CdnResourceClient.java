package steps.t1.cdn;

import core.helper.http.Response;
import io.qameta.allure.Step;
import models.t1.cdn.Resource;
import models.t1.cdn.ResourceListItem;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class CdnResourceClient extends AbstractCdnClient {

    private static final String BASE_PATH = apiUrl + "resources";

    @Step("Создание ресурса")
    public static Resource createResource(String projectId, JSONObject resource) {
        return getRequestSpec()
                .body(resource)
                .post(BASE_PATH, projectId)
                .assertStatus(201)
                .extractAs(Resource.class);
    }

    @Step("Получение всех ресурсов для проекта с id: {0}")
    public static List<ResourceListItem> getResources(String projectId) {
        return getRequestSpec()
                .get(BASE_PATH, projectId)
                .assertStatus(200)
                .jsonPath()
                .getList("list", ResourceListItem.class);
    }

    @Step("Удаление ресурса в проекте: {0}, по одному из доменных имён: {1}")
    public static Response deleteResourceByOneOfDomainName(String projectId, String domainName) {
        String resourceId = getResources(projectId).stream()
                .filter(resource -> Objects.equals(resource.getCname(), domainName))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        String.format("Не найден ни один ресурс с именем %s:", domainName)))
                .getId();

        return deleteResource(projectId, resourceId);
    }

    @Step("Удаление ресурса в проекте: {0}, и с id: {1}")
    private static Response deleteResource(String projectId, String resourceId) {
        return getRequestSpec()
                .delete(BASE_PATH + "/" + resourceId, projectId);
    }
}
