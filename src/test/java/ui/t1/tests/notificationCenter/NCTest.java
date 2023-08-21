package ui.t1.tests.notificationCenter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.BeforeAllExtension;
import ui.t1.pages.supportCenter.MySubscriptionsPage;
import ui.t1.pages.supportCenter.SubscribeUsersPage;

import static com.codeborne.selenide.Selenide.refresh;
import static core.enums.NotificationCenterPriorities.COMMON;
import static core.enums.NotificationCenterPriorities.LOW;

@ExtendWith(BeforeAllExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NCTest extends AbstractNotificationsClass {



    @Test
    @Title("Проверка получения уведомления в разделе Мои уведомления")
    void notificationsPageTest(){
        String eventTime = subscriptionSteps.sendNotification(
                themeCodeOne,
                userEmail,
                "test subject",
                messageURL);
        String themeGroupName =
                subscriptionSteps.getThemeGroupName(groupIDOne).replaceAll("[^A-Za-z0-9]","");
        new IndexPage().goToNotificationCenter();
        notificationsPage.checkMessage(eventTime, themeGroupName, "Тест ws канала")
                    .checkUnReadMark()
                    .clickExpandButton()
                    .checkExpandedMessage("test subject")
                    .checkLink()
                    .clickExpandButton()
                    .checkNoReadMark();
    }

    @Test
    @Title("Проверка работы фильтров и пагинации")
    void filtersTest(){
        subscriptionSteps.sendNumberOfNotifications(5, themeCodeTwo, userEmail, "test 2", messageURL);
        subscriptionSteps.sendNumberOfNotifications(5, themeCodeThree, userEmail, "test 3", messageURL);
        new IndexPage().goToNotificationCenter();
        notificationsPage.checkTableHeaders()
                        .clickFilters()
                        .setThemeGroupFilter(themeGroupNameTwo)
                        .setUnRead()
                        .clickApply()
                        .checkNumberOfNotifications(5)
                        .clickMarkAllRead()
                        .clickResetFilters();
        refresh();
        notificationsPage.checkNumberOfNotifications(0)
                .clickFilters()
                .setPriorityFilter(COMMON.getUiName())
                .setRead()
                .clickApply()
                .checkNumberOfNotifications(10)
                .checkNoReadMark()
                .clickResetFilters()
                .setDate()
                .setNumberOfRows(50)
                .clickApply();
        refresh();
        notificationsPage.checkDateColumn()
                .clickResetFilters()
                .setPriorityFilter(LOW.getUiName())
                .clickApply()
                .checkPriorityColumn()
                .clickMarkAllRead()
                .clickResetFilters();
        refresh();
        notificationsPage.setNumberOfRows(25)
                .checkNumberOfNotifications(25)
                .setNumberOfRows(50)
                .checkNumberOfNotifications(50)
                .setNumberOfRows(10)
                .checkNumberOfNotifications(10)
                .clickNextPage();
    }


    @Test
    @Title("Проверяем работу колокольчика")
    void topBarNotificationTest(){
        String subject = "Тест колокольчика";
        subscriptionSteps.sendNumberOfNotifications(5, themeCodeFour, userEmail, subject, messageURL);
        int counterValue = subscriptionSteps.getUnreadCount();
        notificationsPage.checkUnreadCounter(counterValue)
                        .clickTopBarNotification()
                        .checkNumberOfMessagesTopBar(subject, 5)
                        .checkMessageHeader(subject)
                        .checkImportant()
                        .checkMessageLink()
                        .closeTopMessage()
                        .checkUnreadCounter(counterValue - 5);
        String subject2 = "Второй тест колокольчика";
        subscriptionSteps.sendNumberOfNotifications(5, themeCodeFive, userEmail, subject2, messageURL);
        notificationsPage.checkUnreadCounter(counterValue)
                        .clickTopBarNotification()
                        .checkNoImportant()
                        .clickMessage(subject2);
    }


    @Test
    @Title("Подписываемся и отписываемся от группы тем")
    void subscribeThemeGroupTest(){
        MySubscriptionsPage mySubscriptionsPage = new IndexPage().goToMySubscriptions();
        mySubscriptionsPage
                .createThemeGroupSubscription(uiTestsGroup)
                .checkThemeGroupSubscription("Глобальный", uiTestCodeOne, uiTestCodeTwo)
                .deleteThemeGroupSubscription(uiTestsGroup)
                .checkNoThemeGroupSubscription("Глобальный");
    }

    @Test
    @Title("Подписываемся, редактируем затем удаляем подписку")
    void subscribeSingleThemeTest(){
        MySubscriptionsPage mySubscriptionsPage = new IndexPage().goToMySubscriptions();
        mySubscriptionsPage
                .createThemeSubscription(uiTestCodeOne, uiTestsGroup, "Глобальный")
                .checkThemeSubscription(uiTestCodeOne, "Глобальный")
                .editSubscription(uiTestCodeOne)
                .unSubscribe(uiTestCodeOne);
    }

    @Test
    @Title("Создаем подписку для другого пользователя на Группу тем ") // интерфейс в ближайшее время будет меняться + есть баг из-за которого написание теста пока невозможно.
    void subscribeOtherTest(){
        SubscribeUsersPage subscribeUsersPage = new IndexPage().goToUsersSubscriptions();
        String context = "тест-проект";
        subscribeUsersPage.clickCreateSubscription()
                            .clickGlobal();
        int rowIndex = subscribeUsersPage.getRowIndex(themeGroupNameThree);
        subscribeUsersPage.clickSubscribeThemeGroup(rowIndex);
        subscribeUsersPage
                .createAdminThemeGroupSubscription("review@technoserv.cloud")
                .backToAdminSubscriptionMain()
                .checkAdminSubscriptions(
                        themeGroupNameThree,
                        context,
                        themeCodeThree,
                        themeCodeFour,
                        themeCodeFive,
                        emailTestOne,
                        emailTestTwo,
                        uiTestCodeOne,
                        uiTestCodeTwo);
        subscribeUsersPage
                .clickCreateSubscription()
                .clickUnSubscribeThemeGroup(themeGroupNameThree);
        subscribeUsersPage.unSubscribeThemeGroup(
                context,
                "review@technoserv.cloud",
                themeCodeThree,
                themeCodeFour,
                themeCodeFive,
                emailTestOne,
                emailTestTwo,
                uiTestCodeOne,
                uiTestCodeTwo);
    }

    @Test
    @Title("Проверяем подписку администратора со стороны пользователя")
    void checkAdminSubscriptionTest(){

    }



}
