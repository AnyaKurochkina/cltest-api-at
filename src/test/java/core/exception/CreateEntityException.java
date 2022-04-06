package core.exception;

public class CreateEntityException extends AssertionError {
    public CreateEntityException(String message){
        super(message);
    }
}
