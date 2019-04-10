package com.yao.jdbc;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.sql.SQLClientInfoException;

public class MysqlDemo {

    public static JDBCClient createClient(Vertx vtx){
        JsonObject config = new JsonObject();
//        config.put("url","jdbc://")
        return JDBCClient.createShared(vtx,config);
    }
}
