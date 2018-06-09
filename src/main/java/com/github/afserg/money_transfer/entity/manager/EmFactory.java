package com.github.afserg.money_transfer.entity.manager;

import org.glassfish.hk2.api.Factory;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class EmFactory implements Factory<EntityManager> {
    private final EntityManager em;

    @Inject
    public EmFactory (EntityManagerFactory emf){
        em = emf.createEntityManager();
    }

    @Override
    public EntityManager provide() {
        return em;
    }

    @Override
    public void dispose(EntityManager entityManager) {
        if(entityManager.isOpen()) {
            entityManager.close();
        }
    }
}