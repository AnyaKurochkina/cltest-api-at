package steps.t1.notificationCenterSteps;

import core.enums.Role;
import core.helper.JsonHelper;
import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import steps.Steps;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static core.helper.Configure.getAppProp;

@Log4j2
public class SubscriptionsSteps extends Steps {

    String NCurl = getAppProp("url.notification-center-api");
    String createSubscription = "api/v1/folders/t1-cloud/subscriptions";
    String deleteSubscription = "api/v1/subscriptions/";
    String createTheme = "admin/api/v1/themes";
    String deleteTheme = "admin/api/v1/themes/";
    String sendNotification = "admin/api/v1/events";
    
    @Step("Создаем подписку и получаем ID подписки")
    public String createSubscription(String importance, String themeID, String ... channels){
       JSONObject body = JsonHelper.getJsonTemplate("t1/notificationCenter/createSubscription.json")
                .set("$.importance", Objects.requireNonNull(importance))
                .set("$.channels", Objects.requireNonNull(channels))
                .set("$.theme_id", Objects.requireNonNull(themeID)).build();
        return new Http(NCurl)
                .setRole(Role.CLOUD_ADMIN)
                .body(body)
                .post(createSubscription)
                .assertStatus(201)
                .jsonPath()
                .getString("id");
    }

    @Step("Удаляем подписку")
    public void deleteSubscription(String subscriptionID){
        new Http(NCurl)
                .setRole(Role.CLOUD_ADMIN)
                .delete(deleteSubscription + subscriptionID )
                .assertStatus(204);
    }


    @Step("Создаем тему и получаем ID темы")
    public String createTheme(String code, String name){
        JSONObject body = JsonHelper.getJsonTemplate("t1/notificationCenter/createTheme.json")
                .set("$.code", Objects.requireNonNull(code))
                .set("$.name", Objects.requireNonNull(name)).build();
        return new Http(NCurl)
                .setRole(Role.CLOUD_ADMIN)
                .body(body)
                .post(createTheme)
                .assertStatus(201)
                .jsonPath()
                .getString("id");
    }

    @Step("Удаляем тему")
    public void deleteTheme(String themeID){
        new Http(NCurl)
                .setRole(Role.CLOUD_ADMIN)
                .delete(deleteTheme + themeID)
                .assertStatus(204);
    }


    @Step("Отправляем уведомление")
    public String sendNotification(String themeCode, String email, String subject){
        JSONObject body = JsonHelper.getJsonTemplate("t1/notificationCenter/createEvent.json")
                .set("$.theme_code", Objects.requireNonNull(themeCode))
                .set("$.email", Objects.requireNonNull(email))
                .set("$.subject", Objects.requireNonNull(subject)).build();
        new Http(NCurl)
                .setRole(Role.CLOUD_ADMIN)
                .body(body)
                .post(sendNotification)
                .assertStatus(201);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return formatter.format(LocalDateTime.now());
    }



}
