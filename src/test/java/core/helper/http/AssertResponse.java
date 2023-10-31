package core.helper.http;

import core.utils.AssertUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.opentest4j.AssertionFailedError;
import org.opentest4j.MultipleFailuresError;

@AllArgsConstructor
public class AssertResponse {
    StatusResponseException e;

    @SneakyThrows
    public static AssertResponse run(Executable executable) {
        try {
            executable.execute();
        } catch (StatusResponseException e) {
            return new AssertResponse(e);
        }
        catch (MultipleFailuresError e) {
            for (Throwable throwable : e.getSuppressed())
                if(throwable instanceof StatusResponseException)
                    return new AssertResponse((StatusResponseException) throwable);
        }
        throw new AssertionFailedError("Исключение StatusResponseException не было выброшено");
    }

    public AssertResponse status(int status){
        Assertions.assertEquals(status, e.getStatus());
        return this;
    }

    public AssertResponse responseContains(String text){
        AssertUtils.assertContains(e.getResponseMessage(), text);
        return this;
    }
}
