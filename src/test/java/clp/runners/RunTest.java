package clp.runners;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"json:target/cucumber-report/cucumber.json", "pretty"},
        glue = {"clp.steps"},
        features = {"src/test/resources/features"})
public class RunTest {
}
