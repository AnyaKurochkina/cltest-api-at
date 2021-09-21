package steps.keyCloak;

import core.CacheService;
import core.helper.Configure;
import core.helper.Http;
import core.helper.ObjectPoolService;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import models.authorizer.ServiceAccount;
import models.authorizer.User;
import models.keyCloak.Service;
import models.keyCloak.ServiceAccountToken;
import models.keyCloak.UserToken;

@Log4j2
public class KeyCloakSteps {
    private static final String URL = Configure.getAppProp("host_kk");
    private static final int TOKEN_LIFETIME_SEC = 300;


    /**
     * @return - возвращаем токен
     */
    @Step("Получение нового UserToken")
    public static synchronized String getNewUserToken() {
        //Получение сервис из памяти
        Service service = ObjectPoolService.create(Service.builder()
                        .build());
        //Получение пользователя из памяти
        User user = ObjectPoolService.create(User.builder()
                        .build());
        //Отправка запроса на получение токена
        return new Http(URL)
                .setContentType("application/x-www-form-urlencoded")
                .setWithoutToken()
                .post("auth/realms/Portal/protocol/openid-connect/token",
                        String.format("client_id=%s&client_secret=%s&grant_type=password&username=%s&password=%s",
                                service.clientId, service.clientSecret, user.username, user.password))
                .assertStatus(200)
                .jsonPath()
                .get("access_token");
    }

    /**
     * @return возвращаем токен
     */
    public static synchronized String getUserToken() {
        UserToken userToken = ObjectPoolService.create(UserToken.builder()
                        .build());
        long currentTime = System.currentTimeMillis() / 1000L;

        if (currentTime - userToken.time > TOKEN_LIFETIME_SEC) {
            userToken.token = getNewUserToken();
            userToken.time = currentTime;
            //Если он не Null и время его существования не превышено, то просто возвращаем токен из памяти
        }
        //Сохраняем токен
        userToken.save();
        return userToken.token;
    }

    //    @Step("Получение ServiceAccountToken")
    public static synchronized String getServiceAccountToken(String projectId) {
        ServiceAccount serviceAccount = ObjectPoolService.create(ServiceAccount.builder()
                        .projectId(projectId)
                        .build());
        ServiceAccountToken serviceAccountToken = ObjectPoolService.create(ServiceAccountToken.builder()
                        .serviceAccountName(serviceAccount.name)
                        .build());
        long currentTime = System.currentTimeMillis() / 1000L;
        if (currentTime - serviceAccountToken.time > TOKEN_LIFETIME_SEC) {
            serviceAccountToken.token = getNewServiceAccountToken(serviceAccount);
            serviceAccountToken.time = currentTime;
        }
        serviceAccountToken.save();
        return serviceAccountToken.token;
    }

    @Step("Получение нового ServiceAccountToken")
    public static synchronized String getNewServiceAccountToken(ServiceAccount serviceAccount) {
        return new Http(URL)
                .setContentType("application/x-www-form-urlencoded")
                .setWithoutToken()
                .post("auth/realms/Portal/protocol/openid-connect/token",
                        String.format("client_id=%s&client_secret=%s&grant_type=client_credentials",
                                serviceAccount.name, serviceAccount.secret))
                .assertStatus(200)
                .jsonPath()
                .get("access_token");
    }
}
