package ui.uiExtesions;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Step;
import models.orderService.products.Windows;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import ui.cloud.pages.LoginPage;
import ui.cloud.pages.WindowsPage;

import static com.codeborne.selenide.Selenide.open;

public class PR implements ParameterResolver {
    static Windows product = Windows.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").build();

    @Override
    @Step("supportsParameter")
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return true;
    }

    @Override
    @Step("resolveParameter")
    public Object resolveParameter(ParameterContext parameterContext,
                                   ExtensionContext extensionContext) throws ParameterResolutionException {
        new LoginPage("proj-xazpppulba").singIn();
        open("https://ift-portal-front.apps.d0-oscp.corp.dev.vtb/container/orders/328091e3-7f99-4525-95b3-0c6b2869db6b/main?context=proj-xazpppulba&type=project&org=vtb");
        WindowsPage page = new WindowsPage(product);
        return null;
    }
}