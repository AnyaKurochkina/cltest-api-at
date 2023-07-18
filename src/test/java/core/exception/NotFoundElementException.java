package core.exception;

import core.helper.StringUtils;

public class NotFoundElementException extends AssertionError {
    public NotFoundElementException(String message, Object... params){
        super(StringUtils.format(message, params));
    }

    public NotFoundElementException(Object object){
        super(StringUtils.format("Не найден элемент '{}'", object));
    }
}
