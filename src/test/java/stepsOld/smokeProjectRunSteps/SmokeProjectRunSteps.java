package stepsOld.smokeProjectRunSteps;

import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.fail;

@Log4j2
public class SmokeProjectRunSteps {

    volatile static ConcurrentHashMap<String, String> volatileMap = new ConcurrentHashMap<>();

    @Step("берем данные из мапы по ключу: {key}")
    public void getFromMap(String key) {
        log.info(volatileMap.get(key) + " This thread from GET MAP: "
                    + Thread.currentThread() + " - " + new Date());
    }

    @Step("удаляем данные из мапы по ключу: {key}")
    public void deleteFromMap(String key) {
        log.info(volatileMap.remove(key) + " This thread from DELETE MAP: "
                + Thread.currentThread() + " - " + new Date());
        log.info(volatileMap);
    }

    @Step("загружаем данные в мапу с ключом: {key} и значением: {value}")
    public void loadMap(String key, String value) {
        volatileMap.put(key, value);
        log.info(volatileMap.get(key) + " This thread from PUT MAP: "
                + Thread.currentThread() + " - " + new Date());

    }

    @Step("загружаем данные в мапу с ключом: {key} и значением: {value} failed")
    public void failedScenario(String key, String value) {
       fail("IS FAILED");
        volatileMap.put(key, value);
        log.info(volatileMap.get(key) + " This thread from PUT MAP: "
                + Thread.currentThread() + " - " + new Date());

    }

    @Step("ожидаем")
    public void waitThread() throws InterruptedException {
        Thread.sleep(2000);
    }
}
