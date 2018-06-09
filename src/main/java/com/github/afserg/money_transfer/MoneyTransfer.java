package com.github.afserg.money_transfer;

import com.github.afserg.money_transfer.entity.locker.EntityLock;
import com.github.afserg.money_transfer.entity.locker.EntityLocker;
import com.github.afserg.money_transfer.entity.manager.EmfFactory;
import com.github.afserg.money_transfer.service.AccountService;
import com.github.afserg.money_transfer.entity.manager.EmFactory;
import com.github.afserg.money_transfer.service.MoneyTransferService;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.enterprise.context.RequestScoped;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.net.URI;

public class MoneyTransfer {
    public static final String BASE_URI = "http://localhost:8080/";

    public static HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig().packages("com.github.afserg.money_transfer");
        rc.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(EmfFactory.class).to(EntityManagerFactory.class).in(Singleton.class);
                bindFactory(EmFactory.class).to(EntityManager.class).to(RequestScoped.class);
                bind(MoneyTransferService.class).to(MoneyTransferService.class).in(Singleton.class);
                bind(AccountService.class).to(AccountService.class).in(Singleton.class);
            }
        });
        rc.register(JacksonFeature.class);
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) {
        startServer();
    }
}
