package ru.vtb.opk.apitest.data;

import io.qameta.allure.Step;
import ru.vtb.test.api.helper.FileHelper;


import java.io.IOException;

public class TestData {

    private static String readStepFile(String url) throws IOException {
        return FileHelper.read(url);
    }

    @Step("Создание json параметров для теста")
    public static String createParameterDataForCalculate() {

        return "{JSON file}";
    }
}
