package com.yao.jdbc;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.MySQLClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.UpdateResult;

import java.util.List;


public class MysqlDemo {

    private static AsyncSQLClient createClient(Vertx vtx) {
        // mysql 配置
        JsonObject mysqlConfig = new JsonObject();
        mysqlConfig.put("host", "47.101.195.32")
                .put("port", 3306)
                .put("username", "root")
                .put("password", "123456")
                .put("database", "test")
                .put("maxPoolSize", 5)
                .put("maxConnectionRetries", 3)
                .put("connectTimeout", 10 * 1000)
                .put("connectionRetryDelay", 5 * 1000);
        return MySQLClient.createShared(vtx, mysqlConfig, "myPool");

    }

    public static void main(String[] args) throws InterruptedException {
        Vertx vtx = Vertx.vertx();
        AsyncSQLClient client = createClient(vtx);
        // 异步返回结果
        client.query("select * from person", event -> {
            ResultSet result = event.result();
            List<JsonObject> rows = result.getRows();
            rows.forEach(r->{
                System.out.println("11111111111111111111111");
                String s = "name:" + r.getString("name") + "===age:" + r.getInteger("age");
                System.out.println(s);
            });
        });
        client.update("update person set age=20 where name='zhangsan'",res->{
            System.out.println("222222222222222222222222222");
            UpdateResult result = res.result();
            System.out.println(result.getUpdated());
        });
        client.close();
        System.out.println("333333333333333333333333333333");
        Thread.sleep(5*1000);
//        vtx.close();
    }
}



