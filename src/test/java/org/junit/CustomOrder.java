package org.junit;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.ClassOrdererContext;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.Properties;

public class CustomOrder implements ClassOrderer {

    @Override
    public void orderClasses(ClassOrdererContext classOrdererContext) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("src/test/resources/config/classOrders.properties")) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        classOrdererContext.getClassDescriptors().sort(Comparator.comparingInt(i -> {
            int order = 1;
            String orderProperty = properties.getProperty(i.getTestClass().getName());
            if(orderProperty != null)
                order = Integer.parseInt(orderProperty);
            return order;
        }));

    }
}
