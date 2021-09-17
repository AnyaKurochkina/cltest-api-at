package models;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class Entity {
    public abstract void create();
    public abstract void delete();
}
