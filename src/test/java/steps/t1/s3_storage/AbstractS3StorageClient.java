package steps.t1.s3_storage;

import core.enums.Role;
import core.helper.http.Http;
import io.qameta.allure.Step;
import models.AbstractEntity;
import models.t1.s3_storage.S3Entity;
import models.t1.s3_storage.S3StorageCreateResponse;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.Steps;

public abstract class AbstractS3StorageClient<T extends S3Entity> extends Steps {

    abstract String getS3StorageUrl();

    abstract T getS3Entity(String name, String projectId);

    private static final String API_URL = "/api/v1/";

    @Step("Создание s3 storage с именем: {name}, в проекте: {projectId}")
    public S3StorageCreateResponse createS3(String name, String projectId) {

        S3StorageCreateResponse response = new Http(getS3StorageUrl())
                .setRole(Role.CLOUD_ADMIN)
                .body(new JSONObject(String.format("{\"name\": \"%s\"}", name)))
                .post(API_URL + "projects/{}/buckets", projectId)
                .assertStatus(201)
                .extractAs(S3StorageCreateResponse.class);
        getS3Entity(name, projectId).deleteMode(AbstractEntity.Mode.AFTER_CLASS);
        return response;
    }

    @Step("Добавление верисонирования к s3 storage с именем: {name}, в проекте: {projectId}")
    public void addVersioningToBucketS3(String name, String projectId) {
        String status = new Http(getS3StorageUrl())
                .setRole(Role.CLOUD_ADMIN)
                .body(new JSONObject("{\"status\": \"Enabled\"}"))
                .put(API_URL + "projects/{}/buckets/{}/versioning", projectId, name)
                .assertStatus(200)
                .jsonPath()
                .getString("status");
        Assertions.assertEquals(status, "Enabled", "Статус версионирования должен быть Enabled");
    }

    @Step("Удаление s3 storage с именем: {name}, в проекте: {projectId}")
    public void deleteS3(String name, String projectId) {
        new Http(getS3StorageUrl())
                .setRole(Role.CLOUD_ADMIN)
                .delete(API_URL + "projects/{}/buckets/{}", projectId, name)
                .assertStatus(204);
    }
}
