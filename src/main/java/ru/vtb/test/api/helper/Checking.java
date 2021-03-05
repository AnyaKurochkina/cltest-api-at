package ru.vtb.test.api.helper;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
public class Checking {

    private static final long TIMEOUT_LONG = 20000L;
    private static final long POLING_TIMEOUT = 1000L;

    static CheckingResult checkWithTimeout(Supplier<Integer> actualFunc, final int expected) {
        long startTime = System.currentTimeMillis();
        boolean res = false;
        long actual = -1;
        while ((System.currentTimeMillis() - startTime <= TIMEOUT_LONG) && !res) {
            actual = actualFunc.get();
            res = actual == expected;
            silentSleep(POLING_TIMEOUT);
        }
        CheckingResult result = new CheckingResult(res, String.format("Ожидаемый результат %d - фактический результат %d", expected, actual));
        log.info(result.toString());
        return result;
    }

    public static CheckingResult checkNotEqualsWithTimeout(Supplier<Integer> actualFunc, final int expected) {
        long startTime = System.currentTimeMillis();
        boolean res = false;
        long actual = -1;
        while ((System.currentTimeMillis() - startTime <= TIMEOUT_LONG) && !res) {
            actual = actualFunc.get();
            res = actual != expected;
            silentSleep(POLING_TIMEOUT);
        }
        CheckingResult result = new CheckingResult(res, String.format("Ожидаемый результат %d - фактический результат %d", expected, actual));
        log.info(result.toString());
        return result;
    }

    public static CheckingResult checkWithTimeout(Supplier<String> actualFunc, final String expected) {
        long startTime = System.currentTimeMillis();
        boolean res = false;
        String actual = "";
        while ((System.currentTimeMillis() - startTime <= TIMEOUT_LONG) && !res) {
            actual = actualFunc.get();
            res = !actual.equals(expected);
            silentSleep(POLING_TIMEOUT);
        }
        CheckingResult result = new CheckingResult(res, String.format("Ожидаемый результат %s - фактический результат %s", expected, actual));
        log.info(result.toString());
        return result;
    }

    static CheckingResult checkNotEqualsWithTimeout(Supplier<String> actualFunc, final String expected) {
        long startTime = System.currentTimeMillis();
        boolean res = false;
        String actual = "";
        while ((System.currentTimeMillis() - startTime <= TIMEOUT_LONG) && !res) {
            actual = actualFunc.get();
            res = !actual.equals(expected);
            silentSleep(POLING_TIMEOUT);
        }
        CheckingResult result = new CheckingResult(res, String.format("Ожидаемый результат %s - фактический результат %s", expected, actual));
        log.info(result.toString());
        return result;
    }

    static CheckingResult checkWithoutTimeout(Supplier<Integer> actualFunc, final int expected) {
        boolean res;
        long actual;
        actual = actualFunc.get();
        res = actual == expected;
        CheckingResult result = new CheckingResult(res, String.format("Ожидаемый результат %d - фактический результат %d", expected, actual));
        log.info(result.toString());
        return result;
    }

    public static CheckingResult checkWithoutTimeout(Supplier<String> actualFunc, final String expected) {
        boolean res;
        String actual;
        actual = actualFunc.get();
        res = actual.equals(expected);
        CheckingResult result = new CheckingResult(res, String.format("Ожидаемый результат %s - фактический результат %s", expected, actual));
        log.info(result.toString());
        return result;
    }

    private static void silentSleep(long sec) {
        try {
            Thread.sleep(sec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
