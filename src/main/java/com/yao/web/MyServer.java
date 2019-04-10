package com.yao.web;

import io.vertx.core.Vertx;

public class MyServer {
    public static void main(String[] args) {
        Vertx.vertx()
                .createHttpServer()
                .requestHandler(req->{
                    req.response()
                            .putHeader("content-type","text/plain")
                            .end("hello vert.x");

                })
                .listen(8080);
    }
}
