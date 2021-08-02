package steps.keyCloak;

import core.CacheService;
import core.helper.Configurier;
import core.helper.Http;
import io.qameta.allure.Step;
import models.authorizer.ServiceAccount;
import models.authorizer.User;
import models.keyCloak.Service;
import models.keyCloak.ServiceAccountToken;
import models.keyCloak.UserToken;

public class KeyCloakSteps {
    private static final String URL = Configurier.getInstance().getAppProp("host_kk");
    private static final int TOKEN_LIFETIME_SEC = 300;
    private static final CacheService cacheService = new CacheService();

    @Step("Получение UserToken")
    public static synchronized String getUserToken() {
        UserToken userToken = cacheService.entity(UserToken.class).getEntityWithoutAssert();
        long currentTime  = System.currentTimeMillis() / 1000L;
        if(userToken == null){
            userToken = UserToken.builder()
                    .token(getNewUserToken())
                    .time(currentTime)
                    .build();
        }
        else if(currentTime - userToken.time > TOKEN_LIFETIME_SEC) {
            userToken.token = getNewUserToken();
            userToken.time = currentTime;
        }
        cacheService.saveEntity(userToken);
        return userToken.token;
    }

    @Step("Получение нового UserToken")
    public static synchronized String getNewUserToken() {
            Service service = cacheService.entity(Service.class).getEntity();
            User user = cacheService.entity(User.class).getEntity();
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

    @Step("Получение ServiceAccountToken")
    public static synchronized String getServiceAccountToken(String projectId) {
        ServiceAccount serviceAccount = cacheService.entity(ServiceAccount.class)
                .withField("projectId", projectId)
                .getEntity();
        ServiceAccountToken serviceAccountToken = cacheService.entity(ServiceAccountToken.class)
                .withField("serviceAccountName", serviceAccount.name)
                .getEntityWithoutAssert();
        long currentTime  = System.currentTimeMillis() / 1000L;
        if(serviceAccountToken == null){
            serviceAccountToken = ServiceAccountToken.builder()
                    .token(getNewServiceAccountToken(projectId, serviceAccount))
                    .serviceAccountName(serviceAccount.name)
                    .time(currentTime)
                    .build();
        }
        else if(currentTime - serviceAccountToken.time > TOKEN_LIFETIME_SEC) {
            serviceAccountToken.token = getNewServiceAccountToken(projectId, serviceAccount);
            serviceAccountToken.time = currentTime;
        }
        cacheService.saveEntity(serviceAccountToken);
        return serviceAccountToken.token;
    }

    @Step("Получение нового ServiceAccountToken")
    public static synchronized String getNewServiceAccountToken(String projectId, ServiceAccount serviceAccount) {

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
