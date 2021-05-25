package clp.core.utils;

/**
 * Таймер для тестов
 *
 * @author Max
 */
public class Timer {

    /**
     * Таймаут по умолчанию 1000с
     */
    private static final long DEFAULT_TIMEOUT = 1000000;

    private final long timeout;

    private long start = 0;
    private long stop = 0;

    /**
     * Создать таймер с таймаутом по умолчанию 1000с
     */
    public Timer() {
        this(Timer.DEFAULT_TIMEOUT);
    }

    /**
     * Создать таймер с таймаутом
     *
     * @param timeout Таймаут.
     */
    public Timer(final long timeout) {
        this.timeout = timeout;
    }

    /**
     * Запуск таймера
     *
     * @return Время начала в мс
     */
    public Timer start() {

        this.stop = 0;
        this.start = System.currentTimeMillis();
        return this;
    }

    /**
     * Получить время запуска
     *
     * @return Время запуска таймера.
     */
    public long getStart() {

        return this.start;
    }

    /**
     * Остановка таймера
     *
     * @return Продолжительность (конец - начало)
     */
    public Timer stop() {

        this.stop = System.currentTimeMillis();
        return this;
    }

    /**
     * Получить время остановки
     *
     * @return Время остановки таймера.
     */
    public long getStop() {

        return this.stop;
    }

    /**
     * Получить продолжительность без остановки
     *
     * @return Время активной работы таймера.
     */
    public long getDuration() {

        if (this.stop == 0) {
            return System.currentTimeMillis() - this.start;
        }
        return this.stop - this.start;
    }

    /**
     * Получить таймаут
     *
     * @return Установленный таймаут.
     */
    public long getTimeout() {

        return this.timeout;
    }

    /**
     * Проверить не вышло ли время
     *
     * @return true - не вышло / false - вышло
     */
    public boolean check() {

        return this.getDuration() < this.timeout;
    }
}
