package core.exception;

import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Status;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

import static io.qameta.allure.Allure.getLifecycle;

@Log4j2
public class DeferredException {
    StringBuilder errorStackTrace = new StringBuilder();
    int countError = 0;

    public void addException(Throwable e, String msg) {
        if (e instanceof InvocationTargetException)
            e = e.getCause();
        if(e.toString().startsWith("[1]"))
            errorStackTrace.append(e.getMessage()).append("\n");
        else {
            countError++;
            errorStackTrace.append(String.format("[%d] %s\n %s\n", countError, e, msg));
            Stream.of(e.getStackTrace()).map(StackTraceElement::toString).limit(10).forEach(s -> {
                errorStackTrace.append("\t at ").append(s).append("\n");
            });
            e.printStackTrace();
        }
        AllureLifecycle allureLifecycle = getLifecycle();
        allureLifecycle.getCurrentTestCaseOrStep().ifPresent(id -> allureLifecycle.updateStep(id, s -> s.setStatus(Status.FAILED)));
    }

    public boolean isException() {
        return errorStackTrace.length() > 0;
    }

    public void trowExceptionIfNotEmpty() {
        if (isException())
            throw new AssertionError(toString());
    }

    @Override
    public String toString() {
        return errorStackTrace.toString();
    }

}
