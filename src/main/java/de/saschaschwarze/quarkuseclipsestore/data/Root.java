package de.saschaschwarze.quarkuseclipsestore.data;

import java.util.Map;

import one.microstream.collections.lazy.LazyHashMap;
import one.microstream.storage.types.StorageManager;

public class Root {

    private final Map<String, Product> products = new LazyHashMap<>(); 
    private transient StorageManager storageManager;

    public Map<String, Product> getProducts() {
        return this.products;
    }

    public void addProduct(Product product) {
        this.products.put(product.getId(), product);
        storageManager.store(products);
    }

    public void deleteProduct(String id) {
        this.products.remove(id);
        storageManager.store(products);
    }

    public void updateProduct(Product product) {
        // we assume that the product is already in the List and therefore only store it
        storageManager.store(product);
    }

    public void setStorageManager(StorageManager storageManager) {
        this.storageManager = storageManager;
    }
}
