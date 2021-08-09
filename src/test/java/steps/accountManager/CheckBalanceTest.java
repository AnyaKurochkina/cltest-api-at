package steps.accountManager;

import core.helper.Configurier;
import core.helper.Http;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import tests.Tests;

@Tag("path")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Execution(ExecutionMode.CONCURRENT)
//@OrderLabel("tests.orderService.OrderTest")
public class CheckBalanceTest implements Tests {

    private static final String URL = Configurier.getInstance().getAppProp("host_kong");

    public String getPathToFolder(String target) {
        String path;
        if (target.startsWith("fold")) {
            path = new Http(URL)
                    .get("authorizer/api/v1/folders/" + target + "/path")
                    .assertStatus(200)
                    .jsonPath()
                    .get("data.path");
            return path;
        } else if (target.startsWith("proj")) {
            path = new Http(URL)
                    .get("authorizer/api/v1/projects/" + target + "/path")
                    .assertStatus(200)
                    .jsonPath()
                    .get("data.path");
            return path;
        } else {
            throw new Error("Invalid target: "+ target + "\nYour target must start with \"fold\" or \"proj\"");
        }
    }

    public void getConsumptionByPath(String path) {

        float consumption = new Http(URL)
                .get("calculator/orders/cost/?folder__startswith=" + path)
                .assertStatus(200)
                .jsonPath()
                .get("cost");

        consumption = consumption * 24 * 60;
        System.out.println(consumption);
    }

    @Test
    public void testec() {
        String path = (getPathToFolder("fold-9mmfd1ngom"));
        getConsumptionByPath(path);
    }
}
