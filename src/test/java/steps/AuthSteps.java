package steps;

import core.helper.Configurier;
import core.helper.HttpOld;
import core.vars.LocalThead;
import core.vars.TestVars;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class AuthSteps extends Steps{
    private static final String URL = Configurier.getInstance().getAppProp("host_kk");

    @Step("Получение Token")
    public void getToken() {
        TestVars testVars = LocalThead.getTestVars();
        JsonPath res = new HttpOld(URL)
                .post("auth/realms/Portal/protocol/openid-connect/token", jsonHelper.getStringFromFile("/token.data"))
                .assertStatus(200)
                .jsonPath();
        testVars.setVariables("token", res.get("access_token"));
        testVars.setVariables("token_type", res.get("token_type"));
    }
}
