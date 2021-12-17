package core.utils;

import lombok.extern.log4j.Log4j2;


/**
 * Класс ожиданий. Загрузка элементов, загрузка страницы, возмжность кликнуть и т.п.
 *
 * @author kochetkovma
 */
@Log4j2
public class Waiting {

    private Waiting() {
    }


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
}
