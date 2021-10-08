package models;

import core.helper.JsonHelper;
import core.helper.ObjectPoolEntity;
import core.helper.ObjectPoolService;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.Step;
import io.qameta.allure.model.Parameter;
import lombok.SneakyThrows;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.qameta.allure.Allure.getLifecycle;

public abstract class Entity implements AutoCloseable {

    public String objectClassName;

    public void init(){

    }
//    public abstract JSONObject toJson();
//    public abstract Entity toEntity();
    public abstract void create();

    protected void delete() {
    }


    protected transient JsonHelper jsonHelper = new JsonHelper();
    public String uuid;

    public void save() {
        ObjectPoolService.saveEntity(this);
    }

    @Override
    public void close() {
        ObjectPoolEntity objectPoolEntity = ObjectPoolService.getObjectPoolEntity(this);
        System.out.println(this + " ПреРелиз");
        objectPoolEntity.release();
        System.out.println(this + " ПостРелиз");
    }

    public void deleteObject() {
        ObjectPoolEntity objectPoolEntity = ObjectPoolService.getObjectPoolEntity(this);
//        if(objectPoolEntity == null)
//            System.out.println(1);
        objectPoolEntity.setCreated(false);
        delete();
    }

    public <T extends Entity> T createObject() {
        return createObject(false);
    }

    private <T extends Entity> T createObject(boolean exclusiveAccess) {
        return ObjectPoolService.create(this, exclusiveAccess);
    }

    public <T extends Entity> T createObjectExclusiveAccess() {
        return createObject(true);
    }


//    public abstract void initDefaultFields();
}
