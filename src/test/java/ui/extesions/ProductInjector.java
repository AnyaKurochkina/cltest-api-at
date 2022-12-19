package ui.extesions;

import core.helper.Configure;
import models.cloud.orderService.interfaces.IProduct;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;
import java.util.Objects;


public class ProductInjector implements TestInstancePostProcessor {
    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext extensionContext) throws Exception {
        Field[] fields = testInstance.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (Objects.nonNull(field.get(testInstance)))
                continue;
            Class<?> clazz = field.getType();
            if (IProduct.class.isAssignableFrom(clazz)) {
                IProduct product = (IProduct) clazz.newInstance();

                product.setEnv("DEV");
                if (Configure.ENV.equals("prod")) {
                    product.setEnv("DEV");
                    product.setPlatform("OpenStack");
                }
                else if(Configure.ENV.equals("blue")){
                    product.setPlatform("OpenStack");
                }
                else
                    product.setPlatform("vSphere");
                product.init();

                field.set(testInstance, product);
            }
        }
    }
}
