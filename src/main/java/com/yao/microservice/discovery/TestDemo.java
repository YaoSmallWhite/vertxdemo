package com.yao.microservice.discovery;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;

public class TestDemo extends AbstractVerticle {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(TestDemo.class.getName());

    }

    @Override
    public void start() throws Exception {
        // 创建ServiceDiscovery实例
        ServiceDiscovery discovery = ServiceDiscovery.create(vertx,
                new ServiceDiscoveryOptions()
                        .setAnnounceAddress("service-announce")
                        .setName("test"));
        ProviderDemo providerDemo = new ProviderDemo();
        providerDemo.publish(discovery);

        ConsumerDemo consumerDemo = new ConsumerDemo();
        consumerDemo.consume(discovery);

    }

}
