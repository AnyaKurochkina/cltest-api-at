package steps.t1.s3_storage;

import core.enums.Role;
import core.helper.http.Http;
import io.qameta.allure.Step;
import models.t1.s3_storage.S3StorageCreateResponse;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.Steps;

public abstract class S3StorageClient extends Steps {

    abstract String getS3StorageUrl();

    private static final String apiUrl = "/api/v1/";

    @Step("Создание s3 storage с именем: {name}, в проекте: {projectId}")
    public S3StorageCreateResponse createS3(String name, String projectId) {
        return new Http(getS3StorageUrl())
                .setRole(Role.CLOUD_ADMIN)
                .body(new JSONObject(String.format("{\"name\": \"%s\"}", name)))
                .post(apiUrl + "projects/{}/buckets", projectId)
                .assertStatus(201)
                .extractAs(S3StorageCreateResponse.class);
    }

    @Step("Добавление верисонирования к s3 storage с именем: {name}, в проекте: {projectId}")
    public void addVersioningToBucketS3(String name, String projectId) {
        String status = new Http(getS3StorageUrl())
                .setRole(Role.CLOUD_ADMIN)
                .body(new JSONObject("{\"status\": \"Enabled\"}"))
                .put(apiUrl + "projects/{}/buckets/{}/versioning", projectId, name)
                .assertStatus(200)
                .jsonPath()
                .getString("status");
        Assertions.assertEquals(status, "Enabled", "Статус версионирования должен быть Enabled");
    }

    @Step("Удаление s3 storage с именем: {name}, в проекте: {projectId}")
    public void deleteS3(String name, String projectId) {
        new Http(getS3StorageUrl())
                .setRole(Role.CLOUD_ADMIN)
                .delete(apiUrl + "projects/{}/buckets/{}", projectId, name)
                .assertStatus(204);
    }
}
