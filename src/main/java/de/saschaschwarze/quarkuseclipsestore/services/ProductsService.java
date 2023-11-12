package de.saschaschwarze.quarkuseclipsestore.services;

import java.util.Collection;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.saschaschwarze.quarkuseclipsestore.data.Product;
import de.saschaschwarze.quarkuseclipsestore.exception.ProductAlreadyExistsException;
import de.saschaschwarze.quarkuseclipsestore.exception.ProductNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductsService extends AbstractStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductsService.class);

    private final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();

    public void add(Product product) throws ProductAlreadyExistsException {
        LOGGER.info("add({})", product);
        LOCK.writeLock().lock();
        try {
            if (this.root.getProducts().containsKey(product.getId())) {
                throw new ProductAlreadyExistsException(product.getId());
            }

            this.root.addProduct(product);
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    public Collection<Product> getAll() {
        LOGGER.info("getAll");
        LOCK.readLock().lock();
        try {
            return this.root.getProducts().values();
        } finally {
            LOCK.readLock().unlock();
        }
    }

    public void deleteById(String id) throws ProductNotFoundException {
        LOGGER.info("deleteById({})", id);
        LOCK.writeLock().lock();
        try {
            Product deletedProduct = this.root.getProducts().remove(id);
            if (deletedProduct == null) {
                throw new ProductNotFoundException(id);
            }
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    public Product getById(String id) throws ProductNotFoundException {
        LOGGER.info("getById({})", id);
        LOCK.readLock().lock();
        try {
            Product product = this.root.getProducts().get(id);
            if (product == null) {
                throw new ProductNotFoundException(id);
            }
            return product;
        } finally {
            LOCK.readLock().unlock();
        }
    }
}
