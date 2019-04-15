package com.yao.microservice.discovery;

import io.vertx.core.Vertx;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;
import io.vertx.servicediscovery.types.EventBusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerDemo {
    public static final Logger LOGGER = LoggerFactory.getLogger(ConsumerDemo.class);

    public void consume(ServiceDiscovery discovery) {

        // 消费record
        EventBusService.getProxy(discovery, MyService.class, res -> {
            if (res.succeeded()) {
                String str = res.result().sayHello();
                LOGGER.info("invoke myService successfully and the result is {}" + str);
            } else {
                LOGGER.error("can't find service whose class is MyService");
            }
        });

        discovery.getRecord(r -> r.getName().equals("myService"), res -> {
            if (res.succeeded()) {
                Record result = res.result();
                ServiceReference ref = discovery.getReference(result);
                MyService svc = ref.getAs(MyService.class);
                String str = svc.sayHello();
                LOGGER.info("invoke myService successfully and the result is {}" + str);
                ref.release();
            } else {
                LOGGER.error("can't find service named myService");
            }
        });
    }
}
