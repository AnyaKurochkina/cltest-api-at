package clp.core.infrastructure;

/**
 * Created by ostrzn on 5/26/2017.
 */
public class TestInstanceNotFoundException extends Exception {

    public TestInstanceNotFoundException() {
    }

    public TestInstanceNotFoundException(Throwable cause) {
        super(cause);
    }

    public TestInstanceNotFoundException(String message) {
        super(message);
    }

    public TestInstanceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TestInstanceNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
