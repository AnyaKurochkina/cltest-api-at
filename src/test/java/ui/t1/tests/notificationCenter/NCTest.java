package ui.t1.tests.notificationCenter;

import api.Tests;
import core.enums.Role;
import models.cloud.authorizer.GlobalUser;
import models.cloud.authorizer.Project;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import steps.t1.notificationCenterSteps.SubscriptionsSteps;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.LoginT1Page;
import ui.t1.pages.supportCenter.NotificationsPage;

import static com.codeborne.selenide.Selenide.refresh;
import static core.enums.NotificationCenterPriorities.*;

@ExtendWith(ConfigExtension.class)
//@ExtendWith(BeforeAllExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NCTest extends Tests {
    protected Project project = Project.builder().isForOrders(true).build().createObject();
    GlobalUser user = GlobalUser.builder().role(Role.NOTIFICATIONS_ADMIN).build().createObject();

    SubscriptionsSteps subscriptionSteps = new SubscriptionsSteps();
    NotificationsPage notificationsPage = new NotificationsPage();
    static String groupIDOne;
    static String groupIDTwo;
    static String groupIDThree;
    static String themeIDOne;
    static String themeIDTwo;
    static String themeIDThree;
    static String subscriptionIDOne;
    static String subscriptionIDTwo;
    static String subscriptionIDThree;

    String themeCodeOne = "autotest1";
    String themeCodeTwo = "autotest2";
    String themeCodeThree = "autotest3";

    String themeGroupNameOne = "autotestGroupOne";
    String themeGroupNameTwo = "autotestGroupTwo";
    String themeGroupNameThree = "autotestGroupThree";
    String userEmail = user.getUsername();


    @Title("Создаем темы и группы тем для тестов")
    @BeforeAll
    public void beforeAll(){
        groupIDOne = subscriptionSteps.createThemeGroup(themeGroupNameOne);
        groupIDTwo = subscriptionSteps.createThemeGroup(themeGroupNameTwo);
        groupIDThree = subscriptionSteps.createThemeGroup(themeGroupNameThree);
        themeIDOne = subscriptionSteps.createTheme(themeCodeOne, themeCodeOne, groupIDOne, HIGH.getBackName());
        themeIDTwo = subscriptionSteps.createTheme(themeCodeTwo, themeCodeTwo, groupIDTwo, COMMON.getBackName());
        themeIDThree = subscriptionSteps.createTheme(themeCodeThree, themeCodeThree, groupIDThree, LOW.getBackName());
        subscriptionIDOne = subscriptionSteps.createSubscription(HIGH.getBackName(), themeIDOne, WS.getBackName());
        subscriptionIDTwo = subscriptionSteps.createSubscription(
                COMMON.getBackName(),
                themeIDTwo,
                WS.getBackName(),
                LENTA.getBackName());
        subscriptionIDThree = subscriptionSteps.createSubscription(
                LOW.getBackName(),
                themeIDThree,
                WS.getBackName(),
                LENTA.getBackName());

        subscriptionSteps.markAllRead();
    }




    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginT1Page(project.getId())
                .signIn(Role.NOTIFICATIONS_ADMIN);
    }

    @Test
    @Title("Проверка получения уведомления в разделе Мои уведомления")
    void notificationsPageTest(){
        String eventTime = subscriptionSteps.sendNotification(
                themeCodeOne,
                userEmail,
                "test subject");
        String themeGroupName = subscriptionSteps.getThemeGroupName(groupIDOne).replaceAll("[^A-Za-z0-9]","");
    new IndexPage().goToNotificationCenter();
    notificationsPage.checkMessage(eventTime, themeGroupName, "Запуск сервиса от отработал .")
                    .checkUnReadMark()
                    .clickExpandButton()
                    .checkExpandedMessage("test subject")
                    .checkLink()
                    .clickExpandButton()
                    .checkNoReadMark();
    }

    @Test
    @Title("Проверка работы фильтров и пагинации")
    void filtersTests(){
        subscriptionSteps.sendNumberOfNotifications(5, themeCodeTwo, userEmail, "test 2");
        subscriptionSteps.sendNumberOfNotifications(5, themeCodeThree, userEmail, "test 3");
        new IndexPage().goToNotificationCenter();
        notificationsPage.checkTableHeaders()
                        .clickFilters()
                        .setThemeGroupFilter(themeGroupNameTwo)
                        .setUnRead()
                        .clickApply()
                        .checkNumberOfNotifications(5)
                        .clickMarkAllRead();
        refresh();
        notificationsPage.checkNumberOfNotifications(0)
                .clickResetFilters()
                .setPriorityFilter(COMMON.getUiName())
                .setRead()
                .clickApply()
                .checkNumberOfNotifications(10)
                .checkNoReadMark();
        refresh();
        notificationsPage.checkNumberOfNotifications(0)
                .clickResetFilters()
                .setDate()
                .clickApply();
        refresh();
        notificationsPage.checkDateColumn()
                .clickResetFilters()
                .setPriorityFilter(LOW.getUiName())
                .clickApply()
                .checkPriorityColumn()
                .clickResetFilters();
        refresh();
        notificationsPage.setNumberOfRows(25)
                .checkNumberOfNotifications(25)
                .setNumberOfRows(50)
                .checkNumberOfNotifications(50)
                .setNumberOfRows(10)
                .checkNumberOfNotifications(10);






    }

    @AfterAll
    @Title("Удаляем темы и группы тем, созданные для тестов")
    void afterAll(){
        subscriptionSteps.deleteSubscription(subscriptionIDOne);
        subscriptionSteps.deleteSubscription(subscriptionIDTwo);
        subscriptionSteps.deleteSubscription(subscriptionIDThree);
        subscriptionSteps.deleteTheme(themeIDOne);
        subscriptionSteps.deleteTheme(themeIDTwo);
        subscriptionSteps.deleteTheme(themeIDThree);
        subscriptionSteps.deleteThemeGroup(groupIDOne);
        subscriptionSteps.deleteThemeGroup(groupIDTwo);
        subscriptionSteps.deleteThemeGroup(groupIDThree);


    }

}
