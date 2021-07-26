package tests.suites.KeyCloak;

import core.helper.Configurier;
import core.helper.Http;
import core.helper.HttpOld;
import core.helper.JsonHelper;
import io.qameta.allure.Step;
import tests.suites.Steps;

public class KeyCloakSteps extends Steps {
    private static final String URL = Configurier.getInstance().getAppProp("host_kk");

    @Step("Получение Token")
    public String getToken() {
        return new Http(URL)
                .post("auth/realms/Portal/protocol/openid-connect/token", jsonHelper.getStringFromFile("/token.data"))
                .assertStatus(200)
                .jsonPath()
                .get("access_token");
    }
}
