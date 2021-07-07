package clp.steps;

import clp.core.helpers.Configurier;
import io.restassured.RestAssured;

import io.restassured.specification.RequestSpecification;

public class Specifications {

    public static final String URL_KONG = Configurier.getInstance().getAppProp("host_kong");
    public static final String URL_SS = Configurier.getInstance().getAppProp("host_ss");

    public static RequestSpecification getRequestSpecificationKong() {
        String token = AuthSteps.getBearerToken();
        RestAssured.useRelaxedHTTPSValidation();
        RequestSpecification requestSpecificationspec = RestAssured
            .given()
            .contentType("application/json; charset=UTF-8")
            .header("Authorization", token)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json, text/plain, */*")
            .baseUri(URL_KONG);
        return requestSpecificationspec;
    }

    public static RequestSpecification getRequestSpecificationSS() {
        String token = AuthSteps.getBearerToken();
        RestAssured.useRelaxedHTTPSValidation();
        RequestSpecification requestSpecificationspec = RestAssured
                .given()
                .contentType("application/json; charset=UTF-8")
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .baseUri(URL_SS);
        return requestSpecificationspec;
    }

}
