package tests.accessGroup;

import core.exception.CustomException;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import steps.AccessGroup;
import steps.Hooks;
import steps.OrderServiceSteps;

import java.io.IOException;
import java.util.stream.Stream;

import static core.helper.JsonHelper.shareData;

@Order(2)
public class AccessGroupTest extends Hooks {
    @Test
    @DisplayName("Создание группы доступы для проекта")
    public void CreateAccessGroup() throws IOException, ParseException, CustomException {
        AccessGroup accessGroup = new AccessGroup();
        accessGroup.CreateAccessGroup("DEV","apitestat", "access_group");

    }

}
