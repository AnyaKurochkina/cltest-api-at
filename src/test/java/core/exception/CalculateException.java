package core.exception;

public class CalculateException extends AssertionError {
    public CalculateException(){
        super();
    }
    public CalculateException(String message){
        super(message);
    }
    public CalculateException(String message, Throwable cause){
        super(message, cause);
    }
    public CalculateException(Throwable cause){
        super(cause);
    }
}
