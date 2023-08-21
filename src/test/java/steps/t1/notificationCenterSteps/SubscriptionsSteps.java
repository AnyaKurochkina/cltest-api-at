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
import java.util.List;
import java.util.Objects;

import static core.helper.Configure.getAppProp;

@Log4j2
public class SubscriptionsSteps extends Steps {

    String NCurl = getAppProp("url.kong") + "notificationcenter-service/";
    String createOrganizationsSubscription = "api/v1/organizations/t1-cloud/subscriptions";
    String deleteSubscription = "api/v1/subscriptions/";
    String createTheme = "admin/api/v1/themes";
    String deleteTheme = "admin/api/v1/themes/";
    String sendNotification = "admin/api/v1/events";
    String getThemeGroupName = "api/v1/theme-groups?id=";
    String createThemeGroup = "admin/api/v1/theme-groups";
    String getUnreadIDs = "api/v1/notifications?read_date__isnull=true&channel=ws";
    String markAllRead = "api/v1/notifications/mark_read";
    String unreadCount = "api/v1/notifications/unread-count?channel=ws";
    String createTemplate = "admin/api/v1/templates";
    String getNotifications = "api/v1/notifications?include=total_count&channel=ws&per_page=10";

    @Step("Создаем подписку {themeID} и получаем ID подписки")
    public String createSubscription(String importance, String themeID, String ... channels){
       JSONObject body = JsonHelper.getJsonTemplate("t1/notificationCenter/createSubscription.json")
                .set("$.importance", Objects.requireNonNull(importance))
                .set("$.channels", Objects.requireNonNull(channels))
                .set("$.theme_id", Objects.requireNonNull(themeID)).build();
        return new Http(NCurl)
                .setRole(Role.NOTIFICATIONS_ADMIN)
                .body(body)
                .post(createOrganizationsSubscription)
                .assertStatus(201)
                .jsonPath()
                .getString("id");
    }

    @Step("Удаляем подписку c ID {subscriptionID}")
    public void deleteSubscription(String subscriptionID){
        new Http(NCurl)
                .setRole(Role.NOTIFICATIONS_ADMIN)
                .delete(deleteSubscription + subscriptionID )
                .assertStatus(204);
    }

    @Step("Удаляем подписки")
    public void deleteSubscriptions(String ... subscriptionsIDs){
        for(String id : subscriptionsIDs){
            deleteSubscription(id);
        }
    }


    @Step("Создаем тему и получаем ID темы {code}")
    public String createTheme(String code, String group_id, String importance, String channel, int... template){
        JSONObject body = JsonHelper.getJsonTemplate("t1/notificationCenter/createTheme.json")
                .set("$.code", Objects.requireNonNull(code))
                .set("$.name", Objects.requireNonNull(code))
                .set("$.importance", Objects.requireNonNull(importance))
                .set("$.theme_group_id", Objects.requireNonNull(group_id))
                .set("$.channels", Objects.requireNonNull(channel))
                .set("$.templates", Objects.requireNonNull(template)).build();
        return new Http(NCurl)
                .setRole(Role.NOTIFICATIONS_ADMIN)
                .body(body)
                .post(createTheme)
                .assertStatus(201)
                .jsonPath()
                .getString("id");
    }

    @Step("Удаляем тему c ID {themeID}")
    public void deleteTheme(String themeID){
        new Http(NCurl)
                .setRole(Role.NOTIFICATIONS_ADMIN)
                .delete(deleteTheme + themeID)
                .assertStatus(204);
    }

    @Step("Удаляем темы")
    public void deleteThemes(String ... themeIDs){
        for(String id : themeIDs){
            deleteTheme(id);
        }
    }


    @Step("Отправляем уведомление")
    public String sendNotification(String themeCode, String email, String subject, String url){
        JSONObject body = JsonHelper.getJsonTemplate("t1/notificationCenter/createEvent.json")
                .set("$.theme_code", Objects.requireNonNull(themeCode))
                .set("$.email", Objects.requireNonNull(email))
                .set("$.subject", Objects.requireNonNull(subject))
                .set("$.url", Objects.requireNonNull(url)).build();
        new Http(NCurl)
                .setRole(Role.NOTIFICATIONS_ADMIN)
                .body(body)
                .post(sendNotification)
                .assertStatus(201);
     List<Object> ids = new Http(NCurl)
                .setRole(Role.NOTIFICATIONS_ADMIN)
                .get(getNotifications)
                .jsonPath()
                .getList("list.id");
     String sentDate = new Http(NCurl)
             .setRole(Role.NOTIFICATIONS_ADMIN)
             .get(getNotifications + "&id=" + ids.get(0))
             .jsonPath()
             .getString("list.sent_date").substring(1, 17);


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy\nHH:mm");



        return formatter.format(LocalDateTime.parse(sentDate));
    }

    @Step("Отправляем {amount} уведомлений")
    public void sendNumberOfNotifications(int amount, String themeCode, String email, String subject, String url){
        for(int i = 0; i < amount; i++){
            JSONObject body = JsonHelper.getJsonTemplate("t1/notificationCenter/createEvent.json")
                    .set("$.theme_code", Objects.requireNonNull(themeCode))
                    .set("$.email", Objects.requireNonNull(email))
                    .set("$.subject", Objects.requireNonNull(subject))
                    .set("$.url", Objects.requireNonNull(url)).build();
            new Http(NCurl)
                    .setRole(Role.NOTIFICATIONS_ADMIN)
                    .body(body)
                    .post(sendNotification)
                    .assertStatus(201);
        }
    }

    @Step("Находим название группы тем по ID")
    public String getThemeGroupName(String id){
        return new Http(NCurl)
                .setRole(Role.NOTIFICATIONS_ADMIN)
                .get(getThemeGroupName + id)
                .assertStatus(200)
                .jsonPath()
                .getString("list.name");
    }

    @Step("Создаем группу тем {name}")
    public String createThemeGroup(String name){
        JSONObject body = JsonHelper.getJsonTemplate("t1/notificationCenter/createThemeGroup.json")
                .set("$.name", Objects.requireNonNull(name)).build();
      return new Http(NCurl)
                .setRole(Role.NOTIFICATIONS_ADMIN)
                .body(body)
                .post(createThemeGroup)
                .assertStatus(201)
                .jsonPath()
                .getString("id");
    }


    @Step("Удаляем группу тем с ID {groupID}")
    public void deleteThemeGroup(String groupID){
        new Http(NCurl)
                .setRole(Role.NOTIFICATIONS_ADMIN)
                .delete(createThemeGroup + "/" + groupID)
                .assertStatus(204);
    }

    @Step("Удаляем группы тем")
    public void deleteThemeGroups(String ... groupIDs){
        for(String id : groupIDs){
            deleteThemeGroup(id);
        }
    }

    @Step("Получаем ID непрочитанных сообщений")
    public List<String> getUnreadIDs(){
      return  new Http(NCurl)
                .setRole(Role.NOTIFICATIONS_ADMIN)
                .get(getUnreadIDs)
                .assertStatus(200)
                .jsonPath()
                .getList("list.id");
    }

    @Step("Отмечаем прочитанным все сообщения")
    public void markAllRead(){
        List<String> ids = getUnreadIDs();
        if(ids.size()>0){
        JsonHelper.getJsonTemplate("t1/notificationCenter/markAllRead.json")
                .set("$.ids", Objects.requireNonNull(ids))
                .send(NCurl)
                .setRole(Role.NOTIFICATIONS_ADMIN)
                .patch(markAllRead)
                .assertStatus(200);}
    }

    @Step("Получаем количество непрочитанных сообщений")
    public int getUnreadCount(){
        return new Http(NCurl)
                .setRole(Role.NOTIFICATIONS_ADMIN)
                .get(unreadCount)
                .assertStatus(200)
                .jsonPath()
                .get("unread_count");
    }

    @Step("Получаем ID шаблонов")
    public int createTemplate(String code, String channelID, String json){
        JSONObject body = JsonHelper.getJsonTemplate("t1/notificationCenter/createTemplate.json")
                .set("$.code", code)
                .set("$.channel_id", channelID)
                .set("$.body", json).build();
        return new Http(NCurl)
                .setRole(Role.NOTIFICATIONS_ADMIN)
                .body(body)
                .post(createTemplate)
                .assertStatus(201)
                .jsonPath()
                .get("id");
    }

    @Step("Удаляем шаблон")
    public void deleteTemplate(int id){
        new Http(NCurl)
                .setRole(Role.NOTIFICATIONS_ADMIN)
                .delete(createTemplate + "/" + id)
                .assertStatus(204);
    }





}
