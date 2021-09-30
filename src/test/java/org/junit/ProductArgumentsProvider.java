package org.junit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import core.helper.ObjectPoolService;
import io.qameta.allure.Allure;
import lombok.extern.log4j.Log4j2;
import models.orderService.interfaces.IProduct;
import models.orderService.interfaces.IProductMock;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;
import steps.Steps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public class ProductArgumentsProvider implements ArgumentsProvider, AnnotationConsumer<Source> {
    public final static int PRODUCTS = 0;
    public final static int ENV = 1;
    private final static List<IProduct> orders = getProductList();
    private int variableName;

    @Override
    public Stream provideArguments(ExtensionContext context) {
        List<Arguments> list = new ArrayList<>();
        if (variableName == PRODUCTS) {
            if (!context.getRequiredTestMethod().isAnnotationPresent(Mock.class)) {
                orders.forEach(entity -> {
                    Class<?> c = entity.getClass();
                    list.add(Arguments.of(ObjectPoolService.fromJson(ObjectPoolService.toJson(entity), c)));
                });
            } else {
                orders.forEach(entity -> {
                    list.add(Arguments.of(new IProductMock(entity.toString())));
                });
            }
        } else if (variableName == ENV) {
            AtomicInteger i = new AtomicInteger(1);
            orders.stream()
                    .filter(distinctByKey(IProduct::getEnv))
                    .collect(Collectors.toList())
                    .forEach(entity -> {
                        list.add(Arguments.arguments(entity.getEnv(), String.valueOf(i.getAndIncrement())));
                    });
        }
        return list.stream();
    }

    @Override
    public void accept(Source variableSource) {
        this.variableName = variableSource.value();
    }

    public static List<IProduct> getProductList() {
        List<IProduct> list = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.findAndRegisterModules();
        try {
            Map<String, List<Map>> products = mapper.readValue(new File(Steps.dataFolder +
                            ((System.getProperty("products") != null) ?
                                    "/" + System.getProperty("products") :
                                    "/products") + ".yaml"),
                    new TypeReference<Map<String, List<Map>>>() {
                    });
            final ObjectMapper objectMapper = new ObjectMapper();
            for (Map.Entry<String, List<Map>> e : products.entrySet()) {
                try {
                    Class<?> c = Class.forName("models.orderService.products." + e.getKey());
                    List<String> listAction = findListInMapByKey("actions", e.getValue());
                    List listProduct = findListInMapByKey("options", e.getValue());
                    if (listProduct == null)
                        continue;
                    for (Object orderObj : listProduct) {
                        IProduct product = (IProduct) objectMapper.convertValue(orderObj, c);
                        if (listAction != null)
                            product.setActions(listAction);
                        list.add(product);
                    }
                } catch (ClassNotFoundException classNotFoundException) {
                    classNotFoundException.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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
