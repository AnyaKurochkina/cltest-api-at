package core.helper;

import com.jayway.jsonpath.JsonPath;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static stepsOld.Steps.dataFolder;

public class ShareData {
    static JSONObject data;
    static ReadWriteLock lock = new ReentrantReadWriteLock();

    public synchronized static void put(String s, Object o){
        JsonPath.parse(data).put("$", s, o);
    }

    public synchronized static void putArray(String s, Object o){
        List<Object> list = get(s);
        if(list == null)
            list = new ArrayList<>();
        list.add(o);
        JsonPath.parse(data).put("$", s, list);
    }

    public synchronized static <T> T get(String s){
        return new io.restassured.path.json.JsonPath(data.toJSONString()).get(s);
    }

    public synchronized static String getString(String s){
        return (String) get(s);
    }

    public void load(String file){
        lock.readLock().lock();
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(dataFolder + file));
            data = (JSONObject) obj;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e.getMessage());
        } finally {
            lock.readLock().unlock();
        }
    }
    public void save(String file){
        try {
            lock.writeLock().lock();
            DataFileHelper.write(dataFolder + file, new org.json.JSONObject(data.toJSONString()).toString(4));
        } catch (IOException e) {
            e.printStackTrace();
            throw new Error(e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }

}
