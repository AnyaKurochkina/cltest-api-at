package ui.t1.tests.notificationCenter;

import api.Tests;
import core.enums.Role;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.GlobalUser;
import models.cloud.authorizer.Project;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.BeforeAll;
import ru.testit.annotations.Title;
import steps.t1.notificationCenterSteps.SubscriptionsSteps;
import ui.extesions.ConfigExtension;
import ui.t1.pages.T1LoginPage;
import ui.t1.pages.supportCenter.NotificationsPage;

import static core.enums.NotificationCenterPriorities.*;
import static core.helper.Configure.getAppProp;

@Log4j2
@ExtendWith(ConfigExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AbstractNotificationsClass extends Tests {

    Project project;
    GlobalUser user = GlobalUser.builder().role(Role.NOTIFICATIONS_ADMIN).build().createObject();

    SubscriptionsSteps subscriptionSteps = new SubscriptionsSteps();
    NotificationsPage notificationsPage = new NotificationsPage();


    static String groupIDOne, groupIDTwo, groupIDThree, groupIDFour, uiGroupID;
    static String themeIDOne, themeIDTwo, themeIDThree, themeIDFour, themeIDFive, uiTestThemeOne, uiTestThemeTwo;
    static String emailThemeID, emailThemeIDTwo;
    static String subscriptionIDOne, subscriptionIDTwo, subscriptionIDThree, subscriptionIDFour, subscriptionIDFive;
    static String emailSubOne, emailSubTwo;
    static int templateWSID, templateEmailID;

    String themeCodeOne = "autotest1", themeCodeTwo = "autotest2", themeCodeThree = "autotest3",
            themeCodeFour = "autotest4", themeCodeFive = "autotest5", emailTestOne = "emailTest1",
            emailTestTwo = "emailTest2", uiTestCodeOne = "uiTestCodeOne", uiTestCodeTwo = "uiTestCodeTwo";

    String themeGroupNameOne = "autotestGroupOne", themeGroupNameTwo = "autotestGroupTwo",
            themeGroupNameThree = "autotestGroupThree", themeGroupNameFour = "emailTestsGroup",
            uiTestsGroup = "groupForUI";

    String emailTemplateBody = "<!DOCTYPE html>\n<html lang=\"ru\">\n<head>\n<meta charset=\"UTF-8\">\n"
            + "<title>Тестовая подписка</title>\n</head>\n<body>\n<p>Тест!<br><br>\nТест почты<br>\n" +
            "\n</p>\n</body>\n</html>";
    String wsTemplateBody = "Тест ws канала";
    String userEmail = user.getUsername();
    String messageURL = getAppProp("base.url") + "/t1-disk";


    public AbstractNotificationsClass() {
        project = Project.builder().isForOrders(true).build().createObject();
    }

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new T1LoginPage(project.getId())
                .signIn(Role.NOTIFICATIONS_ADMIN);
    }


    @Title("Создаем темы и группы тем для тестов")
    @BeforeAll
    public void beforeAll() {
        groupIDOne = subscriptionSteps.createThemeGroup(themeGroupNameOne);
        groupIDTwo = subscriptionSteps.createThemeGroup(themeGroupNameTwo);
        groupIDThree = subscriptionSteps.createThemeGroup(themeGroupNameThree);
        groupIDFour = subscriptionSteps.createThemeGroup(themeGroupNameFour);
        uiGroupID = subscriptionSteps.createThemeGroup(uiTestsGroup);
        templateWSID = subscriptionSteps.createTemplate("wsAuto", WS.getBackName(), wsTemplateBody);
        templateEmailID = subscriptionSteps.createTemplate("emailAuto", EMAIL.getBackName(), emailTemplateBody);
        themeIDOne = subscriptionSteps.createTheme(
                themeCodeOne,
                groupIDOne,
                HIGH.getBackName(),
                WS.getBackName(),
                templateWSID);
        themeIDTwo = subscriptionSteps.createTheme(
                themeCodeTwo,
                groupIDTwo,
                COMMON.getBackName(),
                WS.getBackName(),
                templateWSID);
        themeIDThree = subscriptionSteps.createTheme(
                themeCodeThree,
                groupIDThree,
                LOW.getBackName(),
                WS.getBackName(),
                templateWSID);
        themeIDFour = subscriptionSteps.createTheme(
                themeCodeFour,
                groupIDThree,
                HIGH.getBackName(),
                WS.getBackName(),
                templateWSID);
        themeIDFive = subscriptionSteps.createTheme(
                themeCodeFive,
                groupIDThree,
                COMMON.getBackName(),
                WS.getBackName(),
                templateWSID);
        emailThemeID = subscriptionSteps.createTheme(
                emailTestOne,
                groupIDThree,
                HIGH.getBackName(),
                EMAIL.getBackName(),
                templateEmailID);
        emailThemeIDTwo = subscriptionSteps.createTheme(
                emailTestTwo,
                groupIDThree,
                COMMON.getBackName(),
                EMAIL.getBackName(),
                templateEmailID);
        uiTestThemeOne = subscriptionSteps.createTheme(
                uiTestCodeOne,
                uiGroupID,
                HIGH.getBackName(),
                WS.getBackName(),
                templateWSID);
        uiTestThemeTwo = subscriptionSteps.createTheme(
                uiTestCodeTwo,
                uiGroupID,
                COMMON.getBackName(),
                EMAIL.getBackName(),
                templateEmailID);
        subscriptionIDOne = subscriptionSteps.createSubscription(HIGH.getBackName(), themeIDOne, WS.getBackName());
        subscriptionIDTwo = subscriptionSteps.createSubscription(COMMON.getBackName(), themeIDTwo, WS.getBackName());
        subscriptionIDThree = subscriptionSteps.createSubscription(LOW.getBackName(), themeIDThree, WS.getBackName());
        subscriptionIDFour = subscriptionSteps.createSubscription(HIGH.getBackName(), themeIDFour, WS.getBackName());
        subscriptionIDFive = subscriptionSteps.createSubscription(COMMON.getBackName(), themeIDFive, WS.getBackName());
        emailSubOne = subscriptionSteps.createSubscription(HIGH.getBackName(), emailThemeID, EMAIL.getBackName());
        emailSubTwo = subscriptionSteps.createSubscription(COMMON.getBackName(), emailThemeIDTwo, EMAIL.getBackName());
        subscriptionSteps.markAllRead();
    }


    @AfterAll
    @Title("Удаляем темы и группы тем, созданные для тестов")
    void afterAll() {
        subscriptionSteps.deleteSubscriptions(
                subscriptionIDOne,
                subscriptionIDTwo,
                subscriptionIDThree,
                subscriptionIDFour,
                subscriptionIDFive,
                emailSubOne,
                emailSubTwo);
        subscriptionSteps.deleteThemes(
                themeIDOne,
                themeIDTwo,
                themeIDThree,
                themeIDFour,
                themeIDFive,
                emailThemeID,
                emailThemeIDTwo,
                uiTestThemeOne,
                uiTestThemeTwo);
        subscriptionSteps.deleteThemeGroups(
                groupIDOne,
                groupIDTwo,
                groupIDThree,
                groupIDFour,
                uiGroupID);
        subscriptionSteps.deleteTemplate(templateWSID);
        subscriptionSteps.deleteTemplate(templateEmailID);


    }


}
