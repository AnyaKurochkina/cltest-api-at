package core.helper;

import io.qameta.allure.Allure;

import java.util.concurrent.Callable;

public class Report {

    public static <T> T actionStep(String stepName, Callable<T> callable) {
        return Allure.step(stepName, callable::call);
    }

    public static void actionStep(String stepName, Runnable runnable) {
        Allure.step(stepName, runnable::run);
    }

    public static <T> T preconditionStep(String stepName, Callable<T> callable) {
        return actionStep("[Предусловие] " + stepName, callable);
    }

    public static void preconditionStep(String stepName, Runnable runnable) {
        actionStep("[Предусловие] " + stepName, runnable);
    }

    public static <T> T checkStep(String stepName, Callable<T> callable) {
        return actionStep("[Проверка] " + stepName, callable);
    }

    public static void checkStep(String stepName, Runnable runnable) {
        actionStep("[Проверка] " + stepName, runnable);
    }

    public static <T> T techStep(String stepName, Callable<T> callable) {
        return actionStep("[Тех-шаг] " + stepName, callable);
    }

    public static void techStep(String stepName, Runnable runnable) {
        actionStep("[Тех-шаг] " + stepName, runnable);
    }

}
