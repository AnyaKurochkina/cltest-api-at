package org.junit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import core.helper.Configure;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.ObjectPoolService;
import models.orderService.interfaces.IProduct;
import org.json.JSONObject;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;
import ru.testit.properties.TestProperties;
import ru.testit.utils.Configuration;
import steps.Steps;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public class ProductArgumentsProvider implements ArgumentsProvider, AnnotationConsumer<Source> {
    public final static int PRODUCTS = 0;
    public final static int ONE_PRODUCT = 1;
    public final static int ENV = 2;
    private final static List<IProduct> orders = getProductList();
    private int variableName;

    @SneakyThrows
    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
        if (variableName != ENV) {
            return getProducts(context).stream();
        } else {
            List<Arguments> list = new ArrayList<>();
            orders.stream()
                    .filter(distinctByKey(IProduct::getEnv))
                    .collect(Collectors.toList())
                    .forEach(entity -> list.add(Arguments.arguments(entity.getEnv())));
            return list.stream();
        }
    }


    @SneakyThrows
    private List<Arguments> getProducts(ExtensionContext context) {
        List<Arguments> list = new ArrayList<>();
        if (Configure.isIntegrationTestIt()) {
            List<Configuration> confMap = TestProperties.getInstance().getConfigMapsByTest(context.getRequiredTestMethod());
            Class<?> argument = Arrays.stream(context.getRequiredTestMethod().getParameterTypes())
                    .filter(m -> Entity.class.isAssignableFrom((Class<?>) m)).findFirst().orElseThrow(Exception::new);

            for (Configuration configuration : confMap) {
                if (configuration.getConfMap().isEmpty()) {
                    for (Entity entity : orders) {
                        Class<?> c = entity.getClass();
                        if (argument.isInstance(entity)) {
                            Entity e = ObjectPoolService.fromJson(ObjectPoolService.toJson(entity), c);
                            e.setConfigurationId(configuration.getId());
                            list.add(Arguments.of(e));
                            break;
                        }
                    }
                } else {
                    Entity entity = ObjectPoolService.fromJson(new JSONObject(configuration.getConfMap()).toString(), argument);
                    entity.setConfigurationId(configuration.getId());
                    list.add(Arguments.of(entity));
                }
            }
        } else {
            Class<?>[] params = context.getRequiredTestMethod().getParameterTypes();
            Class<?> clazz = null;
            for (Class<?> m : params) {
                if (Entity.class.isAssignableFrom(m)) {
                    clazz = m;
                    break;
                }
            }
            Class<?> finalClazz = clazz;
            for (Entity entity : orders) {
                Class<?> c = entity.getClass();
                if (finalClazz.isInstance(entity)) {
                    list.add(Arguments.of(ObjectPoolService.fromJson(ObjectPoolService.toJson(entity), c)));
                    if (variableName == ONE_PRODUCT)
                        break;
                }
            }
        }
        return list;
    }

    @Override
    public void accept(Source variableSource) {
        this.variableName = variableSource.value();
    }

    public static Map<String, List<Map>> getProductListMap() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.findAndRegisterModules();
        Map<String, List<Map>> stringListMap = null;
        try {
            stringListMap = mapper.readValue(new File(Steps.dataFolder +
                            ((System.getProperty("products") != null) ?
                                    "/" + System.getProperty("products") :
                                    "/products") + ".yaml"),
                    new TypeReference<Map<String, List<Map>>>() {
                    });
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return stringListMap;
    }

    public static List<IProduct> getProductList() {
        List<IProduct> list = new ArrayList<>();
        Map<String, List<Map>> products = getProductListMap();
        final ObjectMapper objectMapper = new ObjectMapper();
        for (Map.Entry<String, List<Map>> e : products.entrySet()) {
            try {
                Class<?> c = Class.forName("models.orderService.products." + e.getKey());
                List listProduct = findListInMapByKey("options", e.getValue());
                if (listProduct == null)
                    continue;
                for (Object orderObj : listProduct) {
                    IProduct product = (IProduct) objectMapper.convertValue(orderObj, c);
                    list.add(product);
                }
            } catch (Exception x) {
                x.printStackTrace();
            }
        }
        return list;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public static List findListInMapByKey(String key, List<Map> list) {
        for (Map m : list) {
            if (m.containsKey(key)) {
                return (List) m.get(key);
            }
        }
        log.info("Невалидный products.yaml");
        System.exit(1);
        return null;
    }

}
