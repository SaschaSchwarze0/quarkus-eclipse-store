package de.saschaschwarze.quarkuseclipsestore.services;

import de.saschaschwarze.quarkuseclipsestore.data.Root;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import one.microstream.storage.types.StorageManager;

public abstract class AbstractStorageService {

    @Inject
    protected StorageManager storageManager;

    protected Root root;

    @PostConstruct
    void init() {
        this.root = (Root) this.storageManager.root();
    }
}
