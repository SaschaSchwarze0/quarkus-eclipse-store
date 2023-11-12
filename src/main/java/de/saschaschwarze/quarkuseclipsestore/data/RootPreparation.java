package de.saschaschwarze.quarkuseclipsestore.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import one.microstream.integrations.quarkus.types.config.StorageManagerInitializer;
import one.microstream.storage.types.StorageManager;

@ApplicationScoped
public class RootPreparation implements StorageManagerInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RootPreparation.class);

    @Override
    public void initialize(StorageManager storageManager) {
        LOGGER.info("Running RootPreparation");

        boolean initRequired = false;

        Root dataRoot = (Root) storageManager.root();

        if (dataRoot == null) {
            LOGGER.info("Initializing a new DataRoot");
            initRequired = true;
            dataRoot = new Root();
        }

        dataRoot.setStorageManager(storageManager);

        if (initRequired) {
            LOGGER.info("Storing a new DataRoot");
            storageManager.setRoot(dataRoot);
            storageManager.storeRoot();

            LOGGER.info("Adding sample data to DataRoot");
            addSampleData(dataRoot);
        }
    }

    private static void addSampleData(Root dataRoot) {
        dataRoot.addProduct(new Product("ab371k", "Chair", 19.99));
        dataRoot.addProduct(new Product("pwn1a8", "Table", 59.99));
    }
}
