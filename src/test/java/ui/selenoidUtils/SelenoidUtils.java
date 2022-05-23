package ui.selenoidUtils;

import com.codeborne.selenide.Configuration;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;

import static core.helper.Configure.getAppProp;
import static core.helper.Configure.getAppPropStartWidth;

@Log4j2
public class SelenoidUtils {

    public static void isRemote() {
        if (Boolean.parseBoolean(getAppProp("webdriver.is.remote", "true"))) {
            log.info("Ui Тесты стартовали на selenoid сервере: " + getAppProp("webdriver.remote.url"));
            Configuration.remote = getAppProp("webdriver.remote.url");
            Map<String, String> capabilitiesProp = getAppPropStartWidth("webdriver.capabilities.");
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilitiesProp.forEach((k, v) -> {
                String prop = k.replaceAll("webdriver.capabilities.", "");
                if (v.equals("true") || v.equals("false"))
                    capabilities.setCapability(prop, Boolean.parseBoolean(v));
                else
                    capabilities.setCapability(prop, v);
            });
            Configuration.browserCapabilities = capabilities;
        } else {
            log.info("Ui Тесты стартовали локально");
        }
    }
}
