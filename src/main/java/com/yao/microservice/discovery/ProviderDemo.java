package com.yao.microservice.discovery;

import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.EventBusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProviderDemo {
    public static final Logger LOGGER = LoggerFactory.getLogger(ProviderDemo.class);

    public void publish(ServiceDiscovery discovery) {

        // 创建EventBusService的record实例
        Record record = EventBusService.createRecord(
                "myService",
                "address",
                MyService.class,
                new JsonObject().put("key", "value")
        );
        // 发布record
        discovery.publish(record, res -> {
            if (res.succeeded()) {
                LOGGER.info("service myService published successfully");
            } else {
                LOGGER.error("service myService published failed");
            }
        });

    }
}
