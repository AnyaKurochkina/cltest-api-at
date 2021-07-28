package testsOld.suites;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.platform.suite.api.SuiteDisplayName;

//@RunWith(JUnitPlatform.class)
//@SelectPackages("tests.smokeTests")
@SuiteDisplayName("Набор тестов работы с мапой")
@Tag("kekus2")
@Order(3)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Suite3 {
    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void test() throws InterruptedException {
        RhelFullAction.map.forEach((key, value) -> System.out.println("1" + ":" + value));
        RhelFullAction.map.put("3", "3");
        System.out.println("test 3");
        Thread.sleep(5000);
    }
    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void test1() throws InterruptedException {
        RhelFullAction.map.forEach((key, value) -> System.out.println("1" + ":" + value));
        RhelFullAction.map.put("3", "3");
        System.out.println("test 3");
        Thread.sleep(5000);
    }
    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void test2() throws InterruptedException {
        RhelFullAction.map.forEach((key, value) -> System.out.println("1" + ":" + value));
        RhelFullAction.map.put("3", "3");
        System.out.println("test 3");
        Thread.sleep(5000);
    }
}
