package steps.keyCloak;

import core.enums.Role;
import core.helper.Configure;
import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import models.authorizer.GlobalUser;
import models.authorizer.ServiceAccount;
import models.keyCloak.*;

import java.util.Objects;

@Log4j2
public class KeyCloakSteps {
    private static final String URL = Configure.getAppProp("url.keycloak");
    private static final int TOKEN_LIFETIME_SEC = 200;

    /**
     * @return - возвращаем токен
     */
    @Step("Получение нового UserToken")
    public static synchronized String getNewUserToken(Role role) {
        //Получение пользователя из памяти
        GlobalUser globalUser = GlobalUser.builder().role(role).build().createObject();
        //Отправка запроса на получение токена
        return new Http(URL)
                .setContentType("application/x-www-form-urlencoded")
                .setWithoutToken()
                .disableAttachmentLog()
                .body(String.format("client_id=portal-front&grant_type=password&username=%s&password=%s",
                                Objects.requireNonNull(globalUser.getUsername()), Objects.requireNonNull(globalUser.getPassword())))
                .post("auth/realms/Portal/protocol/openid-connect/token")
                .assertStatus(200)
                .jsonPath()
                .get("access_token");
    }

    /**
     * @return возвращаем токен
     */
    public static synchronized String getUserToken(Role role) {
        UserToken userToken = UserToken.builder().role(role).build().createObject();
        long currentTime = System.currentTimeMillis() / 1000L;

        if (currentTime - userToken.getTime() > TOKEN_LIFETIME_SEC) {
            userToken.setToken(getNewUserToken(role));
            userToken.setTime(currentTime);
            //Если он не Null и время его существования не превышено, то просто возвращаем токен из памяти
        }
        //Сохраняем токен
        userToken.save();
        return userToken.getToken();
    }

    //    @Step("Получение ServiceAccountToken")
    public static synchronized String getServiceAccountToken(String projectId) {
        ServiceAccount serviceAccount = ServiceAccount.builder().projectId(projectId).build().createObject();
        ServiceAccountToken saToken = ServiceAccountToken.builder().serviceAccountName(serviceAccount.getId()).build().createObject();
        long currentTime = System.currentTimeMillis() / 1000L;
        if (currentTime - saToken.time > TOKEN_LIFETIME_SEC) {
            saToken.token = getNewToken(serviceAccount);
            saToken.time = currentTime;
        }
        saToken.save();
        return saToken.token;
    }

    @Step("Получение нового Token")
    public static synchronized String getNewToken(KeyCloakClient client) {
        return new Http(URL)
                .setContentType("application/x-www-form-urlencoded")
                .setWithoutToken()
                .body(String.format("client_id=%s&client_secret=%s&grant_type=client_credentials",
                                Objects.requireNonNull(client.getId()), Objects.requireNonNull(client.getSecret())))
                .post("auth/realms/Portal/protocol/openid-connect/token")
                .assertStatus(200)
                .jsonPath()
                .get("access_token");
    }

    public static synchronized String getServiceToken() {
        Service service = Service.builder().build().createObject();
        ServiceToken sToken = ServiceToken.builder().build().createObject();
        long currentTime = System.currentTimeMillis() / 1000L;
        if (currentTime - sToken.time > TOKEN_LIFETIME_SEC) {
            sToken.token = getNewToken(service);
            sToken.time = currentTime;
        }
        sToken.save();
        return sToken.token;
    }
}
