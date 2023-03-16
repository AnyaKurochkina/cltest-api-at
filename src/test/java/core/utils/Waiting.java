package core.utils;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import ui.elements.TypifiedElement;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;


/**
 * Класс ожиданий. Загрузка элементов, загрузка страницы, возмжность кликнуть и т.п.
 *
 * @author kochetkovma
 */
@Log4j2
public class Waiting {

    private Waiting() {}

    /**
     * Заснуть на таймаут
     *
     * @param timeout Время ожидания.
     */
    public static void sleep(int timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void find(Supplier<Boolean> b, Duration duration) {
        find(b, duration, "Return false");
    }

    @SneakyThrows
    public static void find(Supplier<Boolean> b, Duration duration, String message) {
        Instant start = Instant.now();
        while(duration.compareTo(Duration.between(start, Instant.now())) > 0){
            if(b.get()) return;
            Waiting.sleep(300);
        }
        throw new TimeoutException(message + ", duration: "+ duration);
    }

    @SneakyThrows
    public static void findWithRefresh(Supplier<Boolean> b, Duration duration) {
        Instant start = Instant.now();
        while(duration.compareTo(Duration.between(start, Instant.now())) > 0){
            Waiting.sleep(500);
            TypifiedElement.refresh();
            if(b.get()) return;
        }
        throw new TimeoutException("Return false, duration: "+ duration);
    }
}
