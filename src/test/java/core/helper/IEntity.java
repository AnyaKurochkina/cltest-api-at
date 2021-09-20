package core.helper;

import models.Entity;

public interface IEntity {
    public void release();
    public <T extends Entity> T get();
    public void set(Entity entity);
}
