package com.yao.web;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class RouterDemo {
    public static void main(String[] args) {
//        server1();
//        server2();
//        server3();
//        server4();
//        server5();
//        server6();
        server7();
    }


    public static void server1() {
        Vertx vtx = Vertx.vertx();
        Router router = Router.router(vtx);
        //创建route，没有设置路径，默认匹配所有路径，一个router可以有多个route
        router.route()
                //给route注册handler 每个进来的request都会经过handler，一个route可以设置多个handler
                .handler(routingContext -> {
                    routingContext.response()
                            .putHeader("content-type", "text/plain")
                            .end("hello router");
                });
        vtx.createHttpServer()
                // 将router注册进server
                .requestHandler(router)
                .listen(8088);
    }

    public static void server2() {
        Vertx vtx = Vertx.vertx();
        Router router = Router.router(vtx);
        Route route = router.route("/index");
        route.handler(routingContext -> {
            System.out.println("route1");
            HttpServerResponse resp = routingContext.response();
            // 设置为ture，因为后面还有handler要写数据，这个只需要设置一次
            resp.setChunked(true);
            resp.write("route1\n");
            // 5秒后调用下一个匹配的handler
            routingContext.vertx().setTimer(5000, event -> {
                routingContext.next();
            });
        })
                .handler(routingContext -> {
                    System.out.println("route2");
                    HttpServerResponse resp = routingContext.response();
                    resp.write("route2\n");
                    // 5秒后调用下一个匹配的handler
                    routingContext.vertx().setTimer(5000, event -> {
                        routingContext.next();
                    });
                })
                .handler(routingContext -> {
                    System.out.println("route3");
                    HttpServerResponse resp = routingContext.response();
                    resp.write("route3\n");
                    // 结束response
                    resp.end();
                });
        vtx.createHttpServer()
                .requestHandler(router)
                .listen(8080);

    }

    public static void server3() {
        BlockingQueue<String> queue = new LinkedBlockingDeque<>();
        Vertx vtx = Vertx.vertx();
        Router router = Router.router(vtx);
        Route route = router.route("/path");
        // 设置handler为blockingHandler，否则进行阻塞操作会报错
        // 等待阻塞操作完成后，会调用下个handler
        route.blockingHandler(routingContext -> {
            try {
                System.out.println("开始阻塞操作");
                queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            routingContext.next();
        });
        vtx.createHttpServer()
                .requestHandler(router)
                .listen(8080);
    }

    public static void server4() {
        BlockingQueue<String> queue = new LinkedBlockingDeque<>();
        Vertx vtx = Vertx.vertx();
        Router router = Router.router(vtx);
        Route route = router.route("/endpoint");
        // 默认情况下，阻塞handler是按顺序执行的，要想她们不按顺序执行，在设置时order参数设false
        route.blockingHandler(routingContext -> {
            try {
                System.out.println("开始阻塞操作");
                queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            routingContext.next();
        }, false)
                .blockingHandler(rc -> {
                    System.out.println("=====");
                }, false);
        vtx.createHttpServer()
                .requestHandler(router)
                .listen(8080);
    }

    public static void server5() {
        Vertx vtx = Vertx.vertx();
        Router router = Router.router(vtx);
        //get请求 name和age为占位符
        //设置请求方式三种方式
        // router.route("/index").method(HttpMethod.POST).method(HttpMethod.GET);
        // router.get("/index");
        // router.route(HttpMethod.POST,"/index");

        Route route = router.route(HttpMethod.GET, "/index/:name/:age");
        route.handler(rc -> {
            HttpServerRequest req = rc.request();
            // 如果请求/index/wangwu/30,就会得到wangwu和30
            System.out.println(req.getParam("name"));
            System.out.println(req.getParam("age"));
            rc.response()
                    .end("hello");
        });
        vtx.createHttpServer()
                .requestHandler(router)
                .listen(8081);

    }

    public static void server6() {
        Vertx vtx = Vertx.vertx();
        Router router = Router.router(vtx);
        Route route = router.route(HttpMethod.GET, "/haha");
        // 可以通过routingContext在同个route的handler之间共享数据
        route.handler(rc -> {
            rc.put("name", "lisi");
            rc.next();
        })
                .handler(rc -> {
                    Object name = rc.get("name");
                    System.out.println(name);
                    rc.response()
                            .end();
                });
        vtx.createHttpServer()
                .requestHandler(router)
                .listen(8080);


    }

    public static void server7(){
        Vertx vtx = Vertx.vertx();
        Router router = Router.router(vtx);
        Route route1 = router.route(HttpMethod.GET, "/error");
        Route route2 = router.route(HttpMethod.GET, "/fail");
        route1.handler(rc -> {
            throw new RuntimeException("出异常啦！");
        })
                // 失败处理器 对应route出异常时调用
                .failureHandler(rc -> {
                    rc.response()
                            .end("error");
                });

        route2.handler(rc->{
            rc.fail(403);
        })
                // 失败处理器，失败时调用
                .failureHandler(rc->{
                    rc.response()
                            .end("fail");
                });
        vtx.createHttpServer()
                .requestHandler(router)
                .listen(8080);

    }
}
