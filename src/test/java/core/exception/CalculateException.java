package core.exception;

public class CalculateException extends AssertionError {
    public CalculateException(Throwable cause){
        super(cause);
    }
}
