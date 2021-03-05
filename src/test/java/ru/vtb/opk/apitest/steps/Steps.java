package ru.vtb.opk.apitest.steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.config.DecoderConfig;
import io.restassured.config.EncoderConfig;
import io.restassured.config.HttpClientConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.aeonbits.owner.ConfigFactory;
import org.apache.http.params.CoreConnectionPNames;
import ru.vtb.test.api.helper.ConfigVars;

import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.given;

@Slf4j
public class Steps {

    private static ConfigVars var = ConfigFactory.create(ConfigVars.class);
    private static final String PHONE_PART = "phone/";
    private static final String ZNIIS_PART = "/v1/integrationZniis/";
    private static final String MNP_PART = "mnp";
    private static final String DEF_PART = "def";

    static {
        RestAssured.config().httpClient(HttpClientConfig.httpClientConfig()
                .setParam(CoreConnectionPNames.CONNECTION_TIMEOUT, 50000)
                .setParam(CoreConnectionPNames.SO_TIMEOUT, 50000)
                .dontReuseHttpClientInstance()
        )
                .getLogConfig().enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.config = RestAssured.config
                .encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset(StandardCharsets.UTF_8))
                .decoderConfig(DecoderConfig.decoderConfig().defaultContentCharset(StandardCharsets.UTF_8));
    }

    static Response postRequest(String body, String myPath) {
        if (body == null || body.isEmpty()) {
            body = "{}";
        }
        log.info("body: " + body);
        Response resp = given().body(body).contentType("application/json;charset=UTF-8").post(myPath);
        log.info(resp.asString());
        Allure.step("response:\n" + resp.asString());
        return resp;
    }

    private static Response getRequest(RequestSpecification spec, String url) {
        Response resp = spec.get(url);
        log.info(resp.asString());
        Allure.step("response:\n" + resp.asString());
        return resp;
    }

    @Step("Получение всех телефонов из списка MNP")
    public static Response getMNP(long size, long page, String sortBy) {
        String url = var.host() + ZNIIS_PART + MNP_PART;
        return getRequest(given().queryParam("size", size).queryParam("page", page).queryParam("sort", sortBy), url);
    }

    @Step("Получение оператора по номеру телефона")
    public static Response getPhone(String phone) {
        String url = var.host() + ZNIIS_PART + PHONE_PART + phone;
        return getRequest(given(), url);
    }

    @Step("Получение всех значений справочника DEF кодов")
    public static Response getDef(long size, long page, String sortBy) {
        String url = var.host() + ZNIIS_PART + DEF_PART;
        return getRequest(given().queryParam("size", size).queryParam("page", page).queryParam("sort", sortBy), url);
    }

    @Step("Получение всех значений справочника DEF кодов без сортировки")
    public static Response getDef(long size, long page) {
        String url = var.host() + ZNIIS_PART + DEF_PART;
        return getRequest(given().queryParam("size", size).queryParam("page", page), url);
    }

}