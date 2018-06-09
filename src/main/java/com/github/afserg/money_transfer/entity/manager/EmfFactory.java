package com.github.afserg.money_transfer.entity.manager;

import org.glassfish.hk2.api.Factory;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EmfFactory implements Factory<EntityManagerFactory> {
    private final EntityManagerFactory emf;

    public EmfFactory(){
        emf = Persistence.createEntityManagerFactory("money-transfer");
    }

    @Override
    public EntityManagerFactory provide() {
        return emf;
    }

    @Override
    public void dispose(EntityManagerFactory entityManagerFactory) {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
