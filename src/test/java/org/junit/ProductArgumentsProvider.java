package org.junit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import models.Entity;
import models.orderService.interfaces.IProduct;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;
import steps.Steps;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class ProductArgumentsProvider implements ArgumentsProvider, AnnotationConsumer<Source> {
    public final static String PRODUCTS = "PRODUCTS";
    public final static String ENV = "ENV";
    private final static List<IProduct> orders = getProductList();
    private String variableName;

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        Set<Arguments> list = new HashSet<>();
        if(variableName.equals(PRODUCTS)) {
            orders.forEach(entity -> {
                list.add(Arguments.arguments(entity));
            });
        }
        else if(variableName.equals(ENV)){
            orders.forEach(entity -> {
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
                        list.add((IProduct) objectMapper.convertValue(orderObj, c));
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
}
