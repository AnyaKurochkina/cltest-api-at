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
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.orderService.interfaces.ProductStatus;
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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.testit.properties.AppProperties.TEST_IT_TOKEN;

@Log4j2
public class ProductArgumentsProvider implements ArgumentsProvider, AnnotationConsumer<Source> {
    public final static int PRODUCTS = 0;
    public final static int ONE_PRODUCT = 1;
    public final static int ENV = 2;
    private final static List<IProduct> orders = getProductList();
    private final static Map<String, List<String>> filter = stringToMap(System.getProperty("ParametrizedFilter"));
    private int variableName;

    @SneakyThrows
    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
        if (variableName != ENV) {
            return getProducts(context).stream();
        } else {
            List<Arguments> list = new ArrayList<>();
            orders.stream()
                    .filter(p -> Objects.nonNull(p.getEnv()))
                    .filter(distinctByKey(IProduct::getEnv))
                    .collect(Collectors.toList())
                    .forEach(entity -> list.add(Arguments.arguments(entity.getEnv())));
            return list.stream();
        }
    }


    @SneakyThrows
    private List<Arguments> getProducts(ExtensionContext context) {
        List<Arguments> list = new ArrayList<>();
        final Class<?>[] parameterTypes = context.getRequiredTestMethod().getParameterTypes();
        String className = context.getRequiredTestMethod().getDeclaringClass().getSimpleName();
        String methodName = context.getRequiredTestMethod().getName();
        Class<?> argument = Arrays.stream(parameterTypes)
                .filter(m -> Entity.class.isAssignableFrom((Class<?>) m)).findFirst().orElseThrow(Exception::new);
        Field mockField = Arrays.stream(context.getRequiredTestClass().getDeclaredFields())
                .filter(method -> method.isAnnotationPresent(Mock.class))
                .filter(method -> argument.isAssignableFrom((Class<?>) method.getType()))
                .findFirst()
                .orElse(null);
        if(Objects.nonNull(mockField)){
            if(!Modifier.isStatic(mockField.getModifiers()))
                throw new Exception("Поле с аннотацией Mock должно быть статическим");
            mockField.setAccessible(true);
            IProduct object = (IProduct) mockField.get(context.getTestClass().orElseThrow(Exception::new));
            object.setStatus(ProductStatus.CREATED);
            ObjectPoolService.addEntity(object);
            list.add(addParameters(object, parameterTypes.length, 1));
            return list;
        }

        if (Configure.isIntegrationTestIt()) {
            List<Configuration> confMap = TestProperties.getInstance().getConfigMapsByTest(context.getRequiredTestMethod());

            for (Configuration configuration : confMap) {
                if (configuration.getConfMap().isEmpty()) {
                    for (Entity entity : orders) {
                        Class<?> c = entity.getClass();
                        if (argument.isInstance(entity)) {
                            Entity e = ObjectPoolService.fromJson(ObjectPoolService.toJson(entity), c);
                            e.setConfigurationId(configuration.getId());
                            list.add(addParameters(e, parameterTypes.length, null));
                            break;
                        }
                    }
                } else {
                    Entity entity = ObjectPoolService.fromJson(new JSONObject(configuration.getConfMap()).toString(), argument);
                    entity.setConfigurationId(configuration.getId());
                    list.add(addParameters(entity, parameterTypes.length, null));
                }
            }
        } else {
            Class<?> clazz = null;
            for (Class<?> m : parameterTypes) {
                if (Entity.class.isAssignableFrom(m)) {
                    clazz = m;
                    break;
                }
            }
            Class<?> finalClazz = clazz;
            int counter = 0;
            for (Entity entity : orders) {
                Class<?> c = entity.getClass();
                if (finalClazz.isInstance(entity)) {
                    final String key = className + "#" + methodName;
                    if(filter.containsKey(key)) {
                        counter++;
                        if(filter.get(key).contains(String.valueOf(counter))) {
                            list.add(addParameters(ObjectPoolService.fromJson(ObjectPoolService.toJson(entity), c), parameterTypes.length, counter));
                            if (variableName == ONE_PRODUCT)
                                break;
                        }
                    }
                    else list.add(addParameters(ObjectPoolService.fromJson(ObjectPoolService.toJson(entity), c), parameterTypes.length, counter));
                    if (variableName == ONE_PRODUCT)
                        break;
                }
            }
        }
        return list;
    }

    private static Arguments addParameters(Object arg, int size, Integer counter) {
        if (size == 1)
            return Arguments.of(arg);
        if (size == 2)
            return Arguments.of(arg, counter);
        return Arguments.of(arg, null, counter);
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
                Class<?> c = Class.forName("models.cloud.orderService.products." + e.getKey());
                List listProduct = findListInMapByKey("options", e.getValue());
                if (listProduct == null) {
                    final IProduct product = (IProduct) objectMapper.convertValue(new HashMap<>(), c);
                    product.setSkip(true);
                    list.add(product);
                    continue;
                }
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

    private static Map<String, List<String>> stringToMap(String input) {
        if(Objects.isNull(input))
            return new HashMap<>();
        if(input.isEmpty())
            return new HashMap<>();
        Map<String, List<String>> resultMap = new HashMap<>();
        String[] keyValuePairs = input.split(";");
        for (String pair : keyValuePairs) {
            String[] parts = pair.split("=");
            if (parts.length == 2) {
                String key = parts[0];
                String[] values = parts[1].split(",");
                List<String> valueList = Stream.of(values)
                        .collect(Collectors.toList());

                resultMap.put(key, valueList);
            }
        }
        return resultMap;
    }
}
