package httpModels.productCatalog;


import java.util.List;

public interface GetListImpl {
    List<ItemImpl> getItemsList();

    MetaImpl getMeta();
}
