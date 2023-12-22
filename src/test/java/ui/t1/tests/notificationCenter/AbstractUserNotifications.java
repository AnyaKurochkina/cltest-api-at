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

import java.util.List;

import static core.enums.NotificationCenterPriorities.*;

@Log4j2
@ExtendWith(ConfigExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AbstractUserNotifications extends Tests {

    Project project;
    GlobalUser user = GlobalUser.builder().role(Role.NOTIFICATIONS_USER).build().createObject();
    SubscriptionsSteps subscriptionSteps = new SubscriptionsSteps();

    static String userGroupID, userThemeIDOne, userThemeIDTwo, userThemeIDThree;
    static int userTemplateID;

    String wsTemplateBody = "Пользовательские тесты";
    String themeGroupName = "UserTests";
    String userThemeCodeOne = "UserOne", userThemeCodeTwo = "UserTwo", userThemeCodeThree = "UserThree";
    List<String> userSubscriptionIDs;
    String userEmail = user.getUsername();

    public AbstractUserNotifications(){
        project = Project.builder().isForOrders(true).build().createObject();
    }

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        new T1LoginPage(project.getId())
                .signIn(Role.NOTIFICATIONS_USER);
    }

    @Title("Создаем подписки для тестов")
    @BeforeAll
    public void beforeAll(){
        userTemplateID = subscriptionSteps.createTemplate("userTestTemplate", WS.getBackName(), wsTemplateBody);
        userGroupID = subscriptionSteps.createThemeGroup(themeGroupName);
        userThemeIDOne = subscriptionSteps.createTheme(
                userThemeCodeOne,
                userGroupID,
                COMMON.getBackName(),
                WS.getBackName(),
                userTemplateID);
        userThemeIDTwo = subscriptionSteps.createTheme(
                userThemeCodeTwo,
                userGroupID,
                HIGH.getBackName(),
                WS.getBackName(),
                userTemplateID);
        userThemeIDThree = subscriptionSteps.createTheme(
                userThemeCodeThree,
                userGroupID,
                LOW.getBackName(),
                WS.getBackName(),
                userTemplateID);
        userSubscriptionIDs = subscriptionSteps.createUsersSubscription(
                HIGH.getBackName(),
                userThemeIDOne,
                userEmail);
    }

    @Title("Удаляем подписки")
    @AfterAll
    public void afterAll(){
        subscriptionSteps.deleteUsersSubscriptions(userSubscriptionIDs);
        subscriptionSteps.deleteThemes(userThemeIDTwo, userThemeIDThree, userThemeIDTwo);
        subscriptionSteps.deleteThemeGroup(userGroupID);
        subscriptionSteps.deleteTemplate(userTemplateID);
    }

}
