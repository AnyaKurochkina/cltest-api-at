package ui.t1.tests.notificationCenter;

import api.Tests;
import core.enums.Role;
import models.cloud.authorizer.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.LoginT1Page;
import steps.t1.notificationCenterSteps.SubscriptionsSteps;

@ExtendWith(ConfigExtension.class)
public class NCTest extends Tests {
    protected Project project = Project.builder().isForOrders(true).build().createObject();
    SubscriptionsSteps subscriptionSteps = new SubscriptionsSteps();

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginT1Page(project.getId())
                .signIn(Role.CLOUD_ADMIN);;
    }

    @Test
    void notificationsPageTest(){
//        String themeID = subscriptionSteps.createTheme("NC page test", "nc test");
//        String subscriptionID = subscriptionSteps.createSubscription("HIGH",
//                themeID,
//                "ws",
//                "lenta",
//                "email" );
        String eventTime = subscriptionSteps.sendNotification(
                "TEST 1",
                "airat.muzafarov@gmail.com",
                "test");
    new IndexPage().goToNotificationCenter();

    }
}
