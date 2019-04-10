package com.yao.core;

import io.vertx.core.Vertx;

public class MyServer {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.createHttpServer()
                .requestHandler(request -> {
                    System.out.println(request.uri());
                    System.out.println(request.absoluteURI());
                    request.response()
                            .end("hello");
                })
                .listen(8088);
    }

}
