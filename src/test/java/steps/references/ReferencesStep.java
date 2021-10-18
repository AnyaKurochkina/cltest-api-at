package steps.references;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import core.helper.Configure;
import core.helper.Http;
import io.qameta.allure.Step;
import models.orderService.interfaces.IProduct;
import models.subModels.Flavor;

import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ReferencesStep {
    public static final String URL = Configure.getAppProp("host_kong");

    @Step("Получение списка flavors для продукта {product}")
    public List<Flavor> getProductFlavorsLinkedList(IProduct product) {
        String jsonArray = new Http(URL)
                .setProjectId(product.getProjectId())
                .get("references/api/v1/pages/?directory__name=flavors&tags=" + product.getProductId())
                .assertStatus(200)
                .toString();

        Type type = new TypeToken<List<Flavor>>() {
        }.getType();
        List<Flavor> list = new Gson().fromJson(jsonArray, type);

        return list.stream().sorted(Comparator.comparing(Flavor::getCpus).thenComparing(Flavor::getMemory)).collect(Collectors.toList());
    }

    @Step("Получение списка flavors по page_filter {pageFilter}")
    public List<Flavor> getFlavorsByPageFilterLinkedList(IProduct product, String pageFilter) {
        String jsonArray = new Http(URL)
                .setProjectId(product.getProjectId())
                .get("references/api/v1/pages/?page_filter=" + pageFilter)
                .assertStatus(200)
                .toString();

        Type type = new TypeToken<List<Flavor>>() {
        }.getType();
        List<Flavor> list = new Gson().fromJson(jsonArray, type);

        return list.stream().sorted(Comparator.comparing(Flavor::getCpus).thenComparing(Flavor::getMemory)).collect(Collectors.toList());
    }
}
