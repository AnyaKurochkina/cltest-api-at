package tests.routes;

import core.helper.http.Path;

import static core.helper.Configure.KONG_URL;

public class SecretServiceAdminAPI implements Api {
    //Список хранилищ секретов
    @Route(method = Method.GET, path = "/v1/engines", status = 200)
    public static Path getV1Engines;

    //Список секретов
    @Route(method = Method.GET, path = "/v1/secrets", status = 200)
    public static Path getV1Secrets;

    //Создание секрета
    @Route(method = Method.POST, path = "/v1/secrets", status = 201)
    public static Path postV1Secrets;

    //Удаление секрета
    @Route(method = Method.DELETE, path = "/v1/secrets/{id}", status = 204)
    public static Path deleteV1SecretsId;

    //Просмотр секрета
    @Route(method = Method.GET, path = "/v1/secrets/{id}", status = 200)
    public static Path getV1SecretsId;

    //Обновление тегов секрета
    @Route(method = Method.PATCH, path = "/v1/secrets/{id}", status = 200)
    public static Path patchV1SecretsId;

    //Список правил доступа для секрета
    @Route(method = Method.GET, path = "/v1/secrets/{secret_id}/access_rules", status = 200)
    public static Path getV1SecretsSecretIdAccessRules;

    //Создание правила доступа для секрета
    @Route(method = Method.POST, path = "/v1/secrets/{secret_id}/access_rules", status = 201)
    public static Path postV1SecretsSecretIdAccessRules;

    //Удаление правила доступа для секрета
    @Route(method = Method.DELETE, path = "/v1/secrets/{secret_id}/access_rules/{id}", status = 204)
    public static Path deleteV1SecretsSecretIdAccessRulesId;

    //Просмотр правила доступа для секрета
    @Route(method = Method.GET, path = "/v1/secrets/{secret_id}/access_rules/{id}", status = 200)
    public static Path getV1SecretsSecretIdAccessRulesId;

    //Обновление правила доступа для секрета
    @Route(method = Method.PATCH, path = "/v1/secrets/{secret_id}/access_rules/{id}", status = 200)
    public static Path patchV1SecretsSecretIdAccessRulesId;

    //Удаление данных для секрета
    @Route(method = Method.DELETE, path = "/v1/secrets/{secret_id}/data", status = 204)
    public static Path deleteV1SecretsSecretIdData;

    //Получение данных для секрета
    @Route(method = Method.GET, path = "/v1/secrets/{secret_id}/data", status = 200)
    public static Path getV1SecretsSecretIdData;

    //Обновление данных для секрета
    @Route(method = Method.PATCH, path = "/v1/secrets/{secret_id}/data", status = 200)
    public static Path patchV1SecretsSecretIdData;

    //Добавление данных для секрета
    @Route(method = Method.POST, path = "/v1/secrets/{secret_id}/data", status = 201)
    public static Path postV1SecretsSecretIdData;

    //Просмотр настройки видимости для секрета
    @Route(method = Method.GET, path = "/v1/secrets/{secret_id}/visibility_conditions", status = 200)
    public static Path getV1SecretsSecretIdVisibilityConditions;

    //Обновление настройки видимости для секрета
    @Route(method = Method.PATCH, path = "/v1/secrets/{secret_id}/visibility_conditions", status = 200)
    public static Path patchV1SecretsSecretIdVisibilityConditions;

    //Список пользователей
    @Route(method = Method.GET, path = "/v1/users", status = 200)
    public static Path getV1Users;

    //Просмотр пользователя
    @Route(method = Method.GET, path = "/v1/users/{id}", status = 200)
    public static Path getV1UsersId;

    @Override
    public String url() {
        return KONG_URL + "secret-service/admin/api";
    }
}
