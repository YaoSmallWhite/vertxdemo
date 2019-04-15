package com.yao.redis;

import io.vertx.core.Vertx;
import io.vertx.core.net.impl.SocketAddressImpl;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;

public class SimpleRedisDemo {
    public static Redis createClient(Vertx vertx){
        RedisOptions options=new RedisOptions()
                .setEndpoint(new SocketAddressImpl("47.101.195.32:6379"));
       return Redis.createClient(vertx,options);
       
    }

    public static void main(String[] args) {
        Vertx vertx=Vertx.vertx();
        Redis client = createClient(vertx);
        RedisAPI api = RedisAPI.api(client);
        api.get("mykey",res->{
            if(res.succeeded()){
                res.result().toString();
            }
        });
        
    }
   
}
