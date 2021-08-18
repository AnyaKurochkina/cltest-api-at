package org.junit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import models.orderService.interfaces.IProduct;
import models.orderService.interfaces.IProductMock;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;
import steps.Steps;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProductArgumentsProvider implements ArgumentsProvider, AnnotationConsumer<Source> {
    public final static int PRODUCTS = 0;
    public final static int ENV = 1;
    private final static List<IProduct> orders = getProductList();
    private int variableName;

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        List<Arguments> list = new ArrayList<>();
        if (variableName == PRODUCTS) {
            if (!context.getRequiredTestMethod().isAnnotationPresent(Mock.class))
                orders.forEach(entity -> {
                    list.add(Arguments.arguments(entity));
                });
            else
                orders.forEach(entity -> {
                    list.add(Arguments.arguments(new IProductMock(entity.toString())));
                });
        } else if (variableName == ENV) {
            orders.stream()
                    .filter(distinctByKey(IProduct::getEnv))
                    .collect(Collectors.toList())
                    .forEach(entity -> {
                list.add(Arguments.arguments(entity.getEnv()));
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
            Map<String, List> products = mapper.readValue(new File(Steps.dataFolder + "/products.yaml"), Map.class);
            final ObjectMapper objectMapper = new ObjectMapper();
            for (Map.Entry<String, List> e : products.entrySet()) {
                try {
                    Class<?> c = Class.forName("models.orderService.products." + e.getKey());
                    for (Object orderObj : e.getValue()) {
                        IProduct product = (IProduct) objectMapper.convertValue(orderObj, c);
                        product.init();
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

}
