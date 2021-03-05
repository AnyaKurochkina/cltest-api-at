package ru.vtb.test.api.helper;

import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import ru.vtb.test.api.helper.json.JsonUtil;

@Slf4j
public class Assertions {

    public static void assertWithAllure(Object expected, Object actual, Object ... others) {
        Allure.step(String.format("Ожидаемый результат %s - фактический результат %s", expected, actual));
        log.info(String.format("Ожидаемый результат %s - фактический результат %s", expected, actual));
        SoftAssert softAssert = new SoftAssert();
        if (others.length % 2 != 0) {
            throw new RuntimeException("Количество аргументов для сравнения не четное");
        }
        for (int i = 0; i < others.length; i++) {
            if (i % 2 != 0) continue;
            softAssert.assertEquals(others[i], others[i+1]);
        }
        softAssert.assertEquals(actual, expected);
        softAssert.assertAll();
    }

    public static void assertWithAllure(JsonNode expected, JsonNode actual) {
        Allure.step(String.format("Ожидаемый результат %s - фактический результат %s", expected.toString(), actual.toString()));
        String res = JsonUtil.compareJson(expected, actual);
        if (res != null) {
            Assert.fail(res);
        }
    }

    public static void assertJsonWithAllure(JsonNode expected, JsonNode actual, int expectedStatusCode, int actualStatusCode) {
        Allure.step(String.format("Ожидаемый результат %s - фактический результат %s", expected.toString(), actual.toString()));
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(expectedStatusCode, actualStatusCode, "statusCode not equals");
        String res = JsonUtil.compareJson(expected, actual);
        softAssert.assertEquals(res, null);
    }

    public static void assertWithAllure(boolean isSuccess, String message) {
        Allure.step(message);
        Assert.assertTrue(isSuccess);
    }

    public static void assertWithAllure(CheckingResult result) {
        Allure.step(result.getMessage());
        Assert.assertTrue(result.isResult());
    }
}