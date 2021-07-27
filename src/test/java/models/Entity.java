package models;
import java.util.UUID;

public abstract class Entity {
    public String objectClassName;
    public String objectUid;
    public void setObjectParams(String className) {
        this.objectClassName = className;
        this.objectUid = UUID.randomUUID().toString();
    }
}
