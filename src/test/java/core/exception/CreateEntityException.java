package core.exception;

public class CreateEntityException extends AssertionError {
    public CreateEntityException(){
        super();
    }
    public CreateEntityException(String message){
        super(message);
    }
    public CreateEntityException(String message, Throwable cause){
        super(message, cause);
    }
    public CreateEntityException(Throwable cause){
        super(cause);
    }
}
