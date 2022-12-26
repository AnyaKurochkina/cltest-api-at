package core.exception;

public class CreateEntityException extends AssertionError {
    public CreateEntityException(String message){
        super(message);
    }

    public CreateEntityException(Throwable e){
        super(String.format("Сущность необходимая для выполнения теста была создана с ошибкой:\n%s", e.toString()), e);
    }
}
