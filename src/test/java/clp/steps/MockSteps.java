package clp.steps;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.ru.Тогда;
import io.cucumber.datatable.DataTable;
import net.jadler.JadlerMocker;
import net.jadler.stubbing.server.jetty.JettyStubHttpServer;
import org.apache.velocity.runtime.parser.ParseException;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import clp.core.exception.CustomException;
import clp.core.helpers.Configurier;
import clp.core.testdata.PrepareBody;
import clp.core.testdata.Templater;
import clp.core.vars.LocalThead;
import clp.core.vars.TestVars;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static net.jadler.Jadler.closeJadler;


public class MockSteps {
    private static final Logger log = LoggerFactory.getLogger(MockSteps.class);
    private Scenario scenario;
    private static final String FILE = "file:";
    private static Configurier configer;
    //private
    private List<JadlerMocker> mockers = new ArrayList<>();


    @After
    public void afterScenario() {
        LocalThead.setTestVars(null);
        if (!mockers.isEmpty()) {
            for (JadlerMocker mocker : mockers) {
                mocker.close();
            }
        }
    }

    @Before
    public void beforeScenario(final Scenario scenario) throws CustomException {
        this.scenario = scenario;
        if (configer == null) {
            configer = Configurier.getInstance();
        }
        configer.loadApplicationPropertiesForSegment();
        closeJadler();
    }




    @Тогда("Включить заглушку на порту (.*) с эндпоинтом (.*) с ответом ([^ \"]*) ?(|с задержкой ответа \\d+ секунд$)")
    public void createStaticMock(String port, String endpoint, String responseBody, String delay, DataTable dataTable) throws IOException, ParseException, CustomException {
        TestVars testVars = LocalThead.getTestVars();
        JadlerMocker mocker = new JadlerMocker(new JettyStubHttpServer(Integer.parseInt(port)));

        String templateBody = new PrepareBody(this.scenario.getUri().replaceFirst(FILE, ""), responseBody).loadBody();
        Map<String, String> headers = dataTable.asMap(String.class, String.class);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            mocker.addDefaultHeader(entry.getKey(), entry.getValue());
        }
        if (!delay.equals("")) {
            int delayInt = Integer.parseInt(delay.replaceAll("\\D+", ""));
            mocker.onRequest().havingPathEqualTo(endpoint).respond().withBody(new Templater(templateBody, testVars.getVariables()).fillTemplate())
                    .withContentType("application/xml").withDelay(delayInt, TimeUnit.SECONDS);
        } else {
            mocker.onRequest().havingPathEqualTo(endpoint).respond().withBody(new Templater(templateBody, testVars.getVariables()).fillTemplate())
                    .withContentType("application/xml");
        }
        mocker.start();
        LocalThead.setTestVars(testVars);

    }

    @Тогда("Включить заглушку на порту (.*) с эндпоинтом (.*) с ответом ([^\\s\"]*) с дефолтными заголовками ?(|с задержкой ответа \\d+ секунд$)")
    public void createStaticMock(String port, String endpoint, String responseBody, String delay) throws IOException, ParseException, CustomException {
        TestVars testVars = LocalThead.getTestVars();
        JadlerMocker mocker = new JadlerMocker(new JettyStubHttpServer(Integer.parseInt(port)));

        String templateBody = new PrepareBody(this.scenario.getUri().replaceFirst(FILE, ""), responseBody).loadBody();
        mocker.onRequest().havingPathEqualTo(endpoint).respond().withBody(new Templater(templateBody, testVars.getVariables()).fillTemplate())
                .withContentType("application/xml");

        if (!delay.equals("")) {
            int delayInt = Integer.parseInt(delay.replaceAll("\\D+", ""));
            mocker.onRequest().havingPathEqualTo(endpoint).respond().withBody(new Templater(templateBody, testVars.getVariables()).fillTemplate())
                    .withContentType("application/xml").withDelay(delayInt, TimeUnit.SECONDS);
        } else {
            mocker.onRequest().havingPathEqualTo(endpoint).respond().withBody(new Templater(templateBody, testVars.getVariables()).fillTemplate())
                    .withContentType("application/xml");
        }
        mocker.start();
        LocalThead.setTestVars(testVars);
    }

    @Тогда("Убедиться что строковые переменные (.*) и (.*) равны")
    public void checkStrings(String var1, String var2){
        TestVars testVars = LocalThead.getTestVars();
        Assert.assertTrue(String.format("%s doesnt equals %s",testVars.getVariables().get(var1),testVars.getVariables().get(var2)),testVars.getVariables().get(var1).equals(testVars.getVariables().get(var2)));
        log.info(String.format("%s equals %s",testVars.getVariables().get(var1),testVars.getVariables().get(var2)));
    }



}