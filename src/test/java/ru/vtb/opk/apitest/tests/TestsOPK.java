package ru.vtb.opk.apitest.tests;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.aeonbits.owner.ConfigFactory;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.vtb.opk.apitest.models.AppVersion;
import ru.vtb.opk.apitest.models.Phone;
import ru.vtb.opk.apitest.models.PhoneRanges;
import ru.vtb.opk.apitest.steps.Steps;
import ru.vtb.test.api.helper.Assertions;
import ru.vtb.test.api.helper.ConfigVars;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


@Slf4j
public class TestsOPK {

    private ConfigVars var = ConfigFactory.create(ConfigVars.class);
    private Dao<Phone, String> phoneDao;
    private Dao<PhoneRanges, String> phoneRangesDao;
    private static final String ROOT_JSON_PATH = "$..";

    @AfterClass
    public void after() throws IOException {
        phoneDao.closeLastIterator();
    }

    @BeforeClass
    public void before() throws SQLException {
        ConnectionSource source = new JdbcConnectionSource(var.dbUrl(), var.dbUser(), var.dbPassword());
        phoneDao = DaoManager.createDao(source, Phone.class);
        phoneRangesDao = DaoManager.createDao(source, PhoneRanges.class);
    }

    @Epic(value = "Проверка доступности integrationZniisLoader")
    @Feature(value = "Пинг integrationZniisLoader")
    @Test(description = "Пинг integrationZniisLoader")
    public void integrationZniisLoaderPingTest() {
        String url = var.host() + "/v1/integrationZniisLoader/actuator/info";
        AppVersion actual = RestAssured.given().get(url).jsonPath().getObject("app", AppVersion.class);
        AppVersion expected = AppVersion.builder().name("unp-integration-zniis-loader").version("0.0.1").build();
        Assertions.assertWithAllure(expected, actual);
    }

    @Epic(value = "Проверка доступности integrationZniis")
    @Feature(value = "Пинг integrationZniis")
    @Test(description = "Пинг integrationZniis")
    public void integrationZniisPingTest() {
        String url = var.host() + "/v1/integrationZniis/actuator/info";
        AppVersion actual = RestAssured.given().get(url).jsonPath().getObject("app", AppVersion.class);
        AppVersion expected = AppVersion.builder().name("unp-integration-zniis").version("0.0.1").build();
        Assertions.assertWithAllure(expected, actual);
    }

    @Epic(value = "Выдача информации по телефону из БД")
    @Feature(value = "Проверка ошибки при указании номера телефона которого нет в базе")
    @Test(description = "Проверка ошибки при указании номера телефона которого нет в базе")
    public void getWrongPhoneTest() {
        String wrongPhone = "0000000000";
        Response resp = Steps.getPhone(wrongPhone);
        Assertions.assertWithAllure("Phone " + wrongPhone + " was not found", resp.asString(), HttpStatus.SC_NOT_FOUND, resp.statusCode());
    }

    @Epic(value = "Выдача информации по телефону из БД")
    @Feature(value = "Получение одного номера телефона из БД и сравнение оператора с REST")
    @Test(description = "Сверка выдачи операторов и телефонов по REST с базой данных")
    public void getPhoneTest() throws Exception {
        Phone existPhone = phoneDao.queryBuilder().limit(1L).offset(1L).query().get(0);
        Response resp = Steps.getPhone(existPhone.getPhone());
        Phone actual = resp.jsonPath().getObject(ROOT_JSON_PATH, Phone.class);
        Assertions.assertWithAllure(existPhone, actual, HttpStatus.SC_OK, resp.statusCode());
    }

    @Epic(value = "Получение всех телефонов из списка MNP")
    @Feature(value = "Сверка выдачи всех (до 150) order by occ телефонов mnp из REST с базой данных")
    @Test(description = "Сверка выдачи всех (до 150) телефонов mnp из REST с базой данных")
    public void checkZniisMNPDataInDBOrderByOperator() throws SQLException {
        Response resp = Steps.getMNP(150, 0, "operator");
        List<Phone> actual = resp.jsonPath().getList("content", Phone.class);
        List<Phone> expected = phoneDao.queryBuilder().limit(150L).orderBy("occ", true).offset(0L).query();
        Assertions.assertWithAllure(expected, actual, HttpStatus.SC_OK, resp.statusCode());
    }

    @Epic(value = "Получение всех телефонов из списка MNP")
    @Feature(value = "Сверка выдачи всех (до 150) order by number телефонов mnp из REST с базой данных")
    @Test(description = "Сверка выдачи всех (до 150) телефонов mnp из REST с базой данных")
    public void checkZniisMNPDataInDBOrderByNumber() throws SQLException {
        Response resp = Steps.getMNP(150, 0, "number");
        List<Phone> actual = resp.jsonPath().getList("content", Phone.class);
        List<Phone> expected = phoneDao.queryBuilder().limit(150L).orderBy("number", true).offset(0L).query();
        Assertions.assertWithAllure(expected, actual, HttpStatus.SC_OK, resp.statusCode());
    }

    @Epic(value = "Получение всех телефонов из списка MNP")
    @Feature(value = "Сверка выдачи первых 3х order by occ значений телефонов mnp из REST с базой данных")
    @Test(description = "Сверка выдачи первых 3х order by occ значений телефонов mnp из REST с базой данных")
    public void checkZniisMNPData3InDBOrderByOperator() throws SQLException {
        Response resp = Steps.getMNP(3, 0, "operator");
        List<Phone> actual = resp.jsonPath().getList("content", Phone.class);
        List<Phone> expected = phoneDao.queryBuilder().limit(3L).offset(0L).orderBy("occ", true).query();
        Assertions.assertWithAllure(expected, actual, HttpStatus.SC_OK, resp.statusCode());
    }

    @Epic(value = "Получение всех телефонов из списка MNP")
    @Feature(value = "Сверка выдачи первых 3х order by number значений телефонов mnp из REST с базой данных")
    @Test(description = "Сверка выдачи первых 3х order by number значений телефонов mnp из REST с базой данных")
    public void checkZniisMNPData3InDBOrderByNumber() throws SQLException {
        Response resp = Steps.getMNP(3, 0, "number");
        List<Phone> actual = resp.jsonPath().getList("content", Phone.class);
        List<Phone> expected = phoneDao.queryBuilder().limit(3L).offset(0L).orderBy("number", true).query();
        Assertions.assertWithAllure(expected, actual, HttpStatus.SC_OK, resp.statusCode());
    }

    @Epic(value = "Получение всех телефонов из списка MNP")
    @Feature(value = "Сверка выдачи 10 order by number значений с предпоследней страницы телефонов mnp из REST с базой данных")
    @Test(description = "Сверка выдачи 10 order by number значений с предпоследней страницы телефонов mnp из REST с базой данных")
    public void checkZniisMNPData10FromLastInDBOrderByNumber() throws SQLException {
        long size = 10;
        long totalElements = phoneDao.countOf();
        long i = totalElements % size > 0 ? 1 : 0;
        long totalPages = (totalElements / size) + i;
        long number = totalPages - 1;
        Response resp = Steps.getMNP(size, number, "number");
        List<Phone> actual = resp.jsonPath().getList("content", Phone.class);
        List<Phone> expected = phoneDao.queryBuilder().limit(size).orderBy("number", true).offset(number * size).query();
        Assertions.assertWithAllure(expected, actual, HttpStatus.SC_OK, resp.statusCode());
    }

    @Epic(value = "Получение всех телефонов из списка MNP")
    @Feature(value = "Сверка выдачи 10 order by number значений с предпоследней страницы телефонов mnp из REST с базой данных")
    @Test(description = "Сверка выдачи 10 order by number значений с предпоследней страницы телефонов mnp из REST с базой данных")
    public void checkZniisMNPData10FromLastInDBOrderByOperator() throws SQLException {
        long size = 10;
        long totalElements = phoneDao.countOf();
        long i = totalElements % size > 0 ? 1 : 0;
        long totalPages = (totalElements / size) + i;
        long number = totalPages - 1;
        Response resp = Steps.getMNP(size, number, "operator");
        List<Phone> actual = resp.jsonPath().getList("content", Phone.class);
        List<Phone> expected = phoneDao.queryBuilder().limit(size).orderBy("occ", true).offset(number * size).query();
        Assertions.assertWithAllure(expected, actual, HttpStatus.SC_OK, resp.statusCode());
    }

    @Epic(value = "Получение значений справочника DEF кодов")
    @Feature(value = "Сверка выдачи всех (до 150) диапазонов номеров из REST с базой данных")
    @Test(description = "Сверка выдачи всех (до 150) диапазонов номеров из REST с базой данных")
    public void checkZniisDefDataInDB() throws SQLException {
        Response resp = Steps.getDef(150, 0);
        List<PhoneRanges> actual = resp.jsonPath().getList("content", PhoneRanges.class);
        List<PhoneRanges> expected = phoneRangesDao.queryBuilder().limit(150L).offset(0L).query();
        Assertions.assertWithAllure(expected, actual, HttpStatus.SC_OK, resp.statusCode());
    }

    @Epic(value = "Получение значений справочника DEF кодов")
    @Feature(value = "Сверка выдачи всех (до 150) диапазонов номеров из REST с базой данных")
    @Test(description = "Сверка выдачи всех (до 150) диапазонов номеров из REST с базой данных")
    public void checkZniisDefDataInDBOrderByNumber() throws SQLException {
        Response resp = Steps.getDef(150, 0);
        List<PhoneRanges> actual = resp.jsonPath().getList("content", PhoneRanges.class);
        List<PhoneRanges> expected = phoneRangesDao.queryBuilder().limit(150L).offset(0L).query();
        Assertions.assertWithAllure(expected, actual, HttpStatus.SC_OK, resp.statusCode());
    }

    @Epic(value = "Получение значений справочника DEF кодов")
    @Feature(value = "Сверка выдачи первых 3х значений диапазонов номеров из REST с базой данных")
    @Test(description = "Сверка выдачи первых 3х значений диапазонов номеров из REST с базой данных")
    public void checkZniisDefData3InDB() throws SQLException {
        Response resp = Steps.getDef(3, 0);
        List<PhoneRanges> actual = resp.jsonPath().getList("content", PhoneRanges.class);
        List<PhoneRanges> expected = phoneRangesDao.queryBuilder().limit(3L).offset(0L).query();
        Assertions.assertWithAllure(expected, actual, HttpStatus.SC_OK, resp.statusCode());
    }

    @Epic(value = "Получение значений справочника DEF кодов")
    @Feature(value = "Сверка выдачи 10 значений с предпоследней страницы диапазонов номеров из REST с базой данных")
    @Test(description = "Сверка выдачи 10 значений с предпоследней страницы диапазонов номеров из REST с базой данных")
    public void checkZniisDefData10FromLastInDB() throws SQLException {
        long size = 10;
        long totalElements = phoneRangesDao.countOf();
        long i = totalElements % size > 0 ? 1 : 0;
        long totalPages = (totalElements / size) + i;
        long number = totalPages - 1;
        Response resp = Steps.getDef(size, number);
        System.out.println(resp.asString());
        List<PhoneRanges> actual = resp.jsonPath().getList("content", PhoneRanges.class);
        List<PhoneRanges> expected = phoneRangesDao.queryBuilder().limit(size).offset(number * size).query();
        Assertions.assertWithAllure(expected, actual, HttpStatus.SC_OK, resp.statusCode());
    }
}