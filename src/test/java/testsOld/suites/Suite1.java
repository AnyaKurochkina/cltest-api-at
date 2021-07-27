package testsOld.suites;
import core.CacheService;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.suite.api.SuiteDisplayName;
import tests.suites.RhelFullAction;


import java.util.stream.Stream;

//@RunWith(JUnitPlatform.class)
//@SelectPackages("tests.smokeTests")
@SuiteDisplayName("Набор тестов работы с мапой")
@Tag("kekus2")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(1)
public class Suite1 {
    CacheService cs = new CacheService();

    @ParameterizedTest
    @MethodSource("dataProviderMethod")
    @Execution(ExecutionMode.CONCURRENT)
    public void test(String s, String s1) throws InterruptedException {

       // cs.setEntity(Project.class, new Entity());
        cs.saveEntity(Project.class, new Project("2"));
        Project p = cs.entity(Project.class)
                .setField("name", "2")
                .getEntity();

        System.out.println(1);
        RhelFullAction.map.forEach((key, value) -> System.out.println("1" + ":" + value));
        RhelFullAction.map.put("1", "1");
        System.out.println("test 1");
        Thread.sleep(5000);
    }

    static Stream<Arguments> dataProviderMethod() {
        return Stream.of
                (Arguments.arguments("some key", "some value"),
                        Arguments.arguments("some key5", "some value5"),
                        Arguments.arguments("some key7", "some value7"));

    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void test1() throws InterruptedException {
        RhelFullAction.map.forEach((key, value) -> System.out.println("1" + ":" + value));
        RhelFullAction.map.put("1", "1");
        System.out.println("test 1");
        Thread.sleep(5000);
    }
    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void test2() throws InterruptedException {
        RhelFullAction.map.forEach((key, value) -> System.out.println("1" + ":" + value));
        RhelFullAction.map.put("1", "1");
        System.out.println("test 1");
        Thread.sleep(5000);
    }
}
