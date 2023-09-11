package ui.t1.tests.notificationCenter;

import core.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.elements.Table;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.BeforeAllExtension;
import ui.t1.pages.supportCenter.MySubscriptionsPage;
import ui.t1.pages.supportCenter.SubscribeUsersPage;

import static com.codeborne.selenide.Selenide.refresh;
import static core.enums.NotificationCenterPriorities.*;

@ExtendWith(BeforeAllExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NCTest {

    @Nested
    @DisplayName("Тесты администратора центра уведомлений")
    class AdminNotificationsTests extends AbstractNotificationsClass {
        @Test
        @Title("Проверка получения уведомления в разделе Мои уведомления")
        void notificationsPageTest() {
            String eventTime = subscriptionSteps.sendNotification(
                    themeCodeOne,
                    userEmail,
                    "test subject",
                    messageURL);
            String themeGroupName =
                    subscriptionSteps.getThemeGroupName(groupIDOne).replaceAll("[^A-Za-z0-9]", "");
            new IndexPage().goToNotificationCenter();
            notificationsPage.checkMessage(eventTime, themeGroupName, "test subject\n" +
                            "Тест ws канала")
                    .checkUnReadMark()
                    .clickExpandButton()
                    .checkExpandedMessage("test subject")
                    .checkLink()
                    .clickExpandButton()
                    .checkNoReadMark();
        }

        @Test
        @Title("Проверка работы фильтров и пагинации")
        void filtersTest() {
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
            notificationsPage.checkNumberOfNotifications(10)
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
        void topBarNotificationTest() {
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
                    .checkUnreadCounter(counterValue);
            String subject2 = "Второй тест колокольчика";
            subscriptionSteps.sendNumberOfNotifications(5, themeCodeFive, userEmail, subject2, messageURL);
            counterValue += 5;
            notificationsPage.checkUnreadCounter(counterValue)
                    .clickTopBarNotification()
                    .checkNoImportant()
                    .clickMessage(subject2)
                    .checkUnreadCounter(counterValue - 1)
                    .clickMessageTwice()
                    .checkUnreadCounter(counterValue - 2);
        }


        @Test
        @Title("Подписываемся и отписываемся от группы тем")
        void subscribeThemeGroupTest() {
            MySubscriptionsPage mySubscriptionsPage = new IndexPage().goToMySubscriptions();
            mySubscriptionsPage
                    .createThemeGroupSubscription(uiTestsGroup)
                    .checkThemeGroupSubscription("Глобальный", uiTestCodeOne, uiTestCodeTwo)
                    .deleteThemeGroupSubscription(uiTestsGroup)
                    .checkNoThemeGroupSubscription("Глобальный", uiTestsGroup);
        }

        @Test
        @Title("Подписываемся, редактируем затем удаляем подписку")
        void subscribeSingleThemeTest() {
            MySubscriptionsPage mySubscriptionsPage = new IndexPage().goToMySubscriptions();
            mySubscriptionsPage
                    .createThemeSubscription(uiTestCodeOne, uiTestsGroup)
                    .checkThemeSubscription(uiTestCodeOne, "Глобальный")
                    .editSubscription(uiTestCodeOne)
                    .unSubscribe(uiTestCodeOne);
        }

        @Test
        @Title("Создаем подписку для другого пользователя на Группу тем ")
        void subscribeOtherTest() {
            SubscribeUsersPage subscribeUsersPage = new IndexPage().goToUsersSubscriptions();
            subscribeUsersPage.clickCreateSubscription()
                    .clickGlobal();
            subscribeUsersPage.clickSubscribeThemeGroup(themeGroupNameThree);
            subscribeUsersPage
                    .createAdminThemeGroupSubscription("test@t1cloud.dev")
                    .backToAdminSubscriptionMain()
                    .checkAdminSubscriptions(
                            "test@t1cloud.dev",
                            userEmail,
                            themeCodeThree,
                            themeCodeFour,
                            themeCodeFive,
                            emailTestOne,
                            emailTestTwo);
            subscribeUsersPage.unSubscribeThemeGroup(
                    "test@t1cloud.dev",
                    themeCodeThree,
                    themeCodeFour,
                    themeCodeFive,
                    emailTestOne,
                    emailTestTwo);
        }

    }

    @Nested
    @DisplayName("Пользовательские тесты центра уведомлений")
    class UserNotificationsTests extends AbstractUserNotifications{
        @Test
        @Title("Проверяем подписку администратора со стороны пользователя")
        void checkAdminSubscriptionTest() {
            MySubscriptionsPage mySubscriptionsPage = new IndexPage().goToMySubscriptions();
            mySubscriptionsPage.clickContextButton("Глобальный");
            Table table = new Table("Группа тем");
            table.isColumnValueContains("Группа тем", userGroupID);
            table.isColumnValueContains("Тема", userThemeIDOne);
            table.isColumnValueContains("Создатель", Role.NOTIFICATIONS_ADMIN.name());
            table.isColumnValueContains("Приоритет", HIGH.getUiName());
            table.isColumnValueContains("Каналы отправки", WS.getUiName() + "\n" + EMAIL.getUiName());

        }

    }







}
