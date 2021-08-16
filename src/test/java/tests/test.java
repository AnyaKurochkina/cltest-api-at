package tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class test {

    @Test
    public void test() {
        HashMap<String, String> mapka = new HashMap<>();
        mapka.put("1key", "1value");
        mapka.put("2key", "2value");
        mapka.put("3key", "3value");

        HashMap<String, String> mapka2 = new HashMap<>();
        mapka2.put("1key", "1value");
        mapka2.put("2key", "2value");
        mapka2.put("3key", "4value");
        for (Map.Entry<String, String> entry : mapka.entrySet()) {
            String key = entry.getKey();
            for (Map.Entry<String, String> entry2 : mapka2.entrySet()) {
                String key2 = entry2.getKey();
                if (key.equals(key2)) {
                    Assertions.assertEquals(mapka.get(key), mapka2.get(key2));
                    System.out.println(key + " equeals " + key2);
                }
                System.out.println(key + " not equeals " + key2);
            }
        }

    }

}

