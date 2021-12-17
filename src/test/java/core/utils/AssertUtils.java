package core.utils;

import org.junit.jupiter.api.Assertions;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AssertUtils {

    public static void AssertDate(Date expected, Date actual, int deltaSec){
        long diff = TimeUnit.SECONDS.convert(Math.abs(expected.getTime() - actual.getTime()), TimeUnit.MILLISECONDS);
        Assertions.assertTrue(diff <= deltaSec);
    }
}
