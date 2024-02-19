package steps.t1.cdn;

import io.qameta.allure.Step;
import models.t1.cdn.ResourceListItem;
import tests.routes.cdn.CdnResourceApi;

import java.util.List;
import java.util.Objects;

public class CdnResourceClient extends AbstractCdnClient {

    @Step("Получение всех ресурсов для проекта с id: {0}")
    public static List<ResourceListItem> getResources(String projectId) {
        return getRequestSpec().api(CdnResourceApi.getResources, projectId)
                .jsonPath()
                .getList("list", ResourceListItem.class);
    }

    @Step("Удаление ресурса в проекте: {0}, по одному из доменных имён: {1}")
    public static void deleteResourceByOneOfDomainName(String projectId, String domainName) {
        String resourceId = getResources(projectId).stream()
                .filter(resource -> Objects.equals(resource.getCname(), domainName))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        String.format("Не найден ни один ресурс с именем %s:", domainName)))
                .getId();

        deleteResource(projectId, resourceId);
    }

    @Step("Активация пресета LIVE STEAMING у ресурсас с id: {1}, в проекте: {0} по одному из доменных имён: {1}")
    public static void enableLiveStreamingByName(String projectId, String domainName) {
        String resourceId = getResources(projectId).stream()
                .filter(resource -> Objects.equals(resource.getCname(), domainName))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        String.format("Не найден ни один ресурс с именем %s:", domainName)))
                .getId();

        enableLiveStreaming(projectId, resourceId);
    }

    @Step("Активация пресета LIVE STEAMING у ресурса с id: {1}, в проекте: {0}")
    private static void enableLiveStreaming(String projectId, String resourceId) {
        getRequestSpec().api(CdnResourceApi.enableLiveStreaming, projectId, resourceId);
    }

    @Step("Удаление ресурса в проекте: {0}, и с id: {1}")
    private static void deleteResource(String projectId, String resourceId) {
        getRequestSpec().api(CdnResourceApi.deleteResourceById, projectId, resourceId);
    }
}
