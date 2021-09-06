package core.exception;

import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

public class DeferredException {
    StringBuilder errorStackTrace = new StringBuilder();
    int countError = 0;

    public void addException(Throwable e, String msg) {
        countError++;
        if (e instanceof InvocationTargetException)
            e = e.getCause();
        errorStackTrace.append(String.format("[%d] %s\n %s\n", countError, e.toString(), msg));
        Stream.of(e.getStackTrace()).map(StackTraceElement::toString).forEach(s -> {
            errorStackTrace.append("\t at ").append(s).append("\n");
        });

        errorStackTrace.append("\n");
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
