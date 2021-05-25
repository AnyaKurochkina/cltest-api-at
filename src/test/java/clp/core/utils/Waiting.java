package clp.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BooleanSupplier;


/**
 * Класс ожиданий. Загрузка элементов, загрузка страницы, возмжность кликнуть и т.п.
 *
 * @author kochetkovma
 */
public class Waiting {
    private static final Logger log = LoggerFactory.getLogger(Waiting.class);

    private Waiting() {
    }


    /**
     * Заснуть на таймаут
     *
     * @param timeout Время ожидания.
     */
    public final static void sleep(int timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Ожидание выполнения Предиката predicate<br>
     * В Предикате реализцется любой код, который должен возвращать true в случае удачного выполнения и false в случае неудачного<br>
     * Метод ожидает выполнения Предиката в пределах таймаута и периода повторной проверки значения<br>
     * Опционально можно задать предикаты failConditions - если хотябы один из них будет true, то ожидание зафейлится. Например: один из них проверяет появления
     * сообщения "ОШИБКА". Если это сообщение появится во время ожидания, то все ожидание зафейлится
     *
     * @param predicate      Выражение для проверки времени выполнения
     * @param timeout        Таймаут мс
     * @param period         Период повтора
     * @param failConditions Опционально. Предикаты моментального завершения ожидания с результатом false. Предикат срабатывает если возвращает true
     * @return True, если предикат выполнился успешно; false, если нет.
     */
    public final static boolean wait(BooleanSupplier predicate, int timeout, int period, BooleanSupplier... failConditions) {

        Timer timer = new Timer(timeout);
        timer.start();
        log.debug("Старт ожидания. Таймаут : {}", timeout);
        while (!checkConditions(predicate) && timer.check()) {

            if (Waiting.checkConditions(failConditions)) {
                log.debug("Сработало условие проваленного ожидания");
                return false;
            }
            Waiting.sleep(period);
        }
        timer.stop();
        log.debug("Стоп ожидания. Таймер {}. Результат : {}", timer.getDuration(), timer.check());
        return timer.check();
    }

    private final static boolean checkConditions(BooleanSupplier... failConditions) {
        try {
            for (BooleanSupplier booleanSupplier : failConditions) {
                if (booleanSupplier.getAsBoolean()) {
                    return true;
                }
            }
        } catch (Throwable error) {
            log.error("Ошибка во время ожидания. Будет пропущена. Ожидание продолжается {}", error.getMessage());
        }
        return false;
    }
}
