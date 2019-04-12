package com.yao.mongo;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.UpdateOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDemo.class);

    public static void main(String[] args) throws InterruptedException {
        Vertx vertx = Vertx.vertx();
        MongoClient client = createClient(vertx);
//        saveOpration(client);
//        insertOperation(client);
//        updateOperation(client);
//        replaceOperation(client);
        findOperation(client);
        Thread.sleep(5 * 1000);


    }

    public static MongoClient createClient(Vertx vtx) {
        JsonObject mongoConfig = new JsonObject();
        mongoConfig
                .put("connection_string", "mongodb://47.101.195.32:27017")
                .put("db_name", "test")
                .put("maxPoolSize", 10)
                .put("minPoolSize", 5)
                .put("maxIdleTimeMS", 5 * 60 * 1000)
                .put("maxLifeTimeMS", 60 * 60 * 1000)
                .put("waitQueueTimeoutMS", 10 * 1000)
                .put("keepAlive", true);
        MongoClient client = MongoClient.createShared(vtx, mongoConfig);
        return client;
    }

    public static void saveOpration(MongoClient client) {
        // 保存文档
        JsonObject book1 = new JsonObject();
        book1.put("title", "think in java")
                .put("_id", "1");
        client.save("books", book1, res -> {
            if (res.succeeded()) {
                LOGGER.info("save document successfully");
            } else {
                LOGGER.error("save document failed");
            }
        });
        // 如果_id已存在,文档会更新
        JsonObject book2 = new JsonObject()
                .put("title", "effective java")
                .put("_id", "1");
        client.save("books", book2, res -> {
            if (res.succeeded()) {
                LOGGER.info("save document successfully");
            } else {
                LOGGER.error("save document failed");
            }
        });
    }

    public static void insertOperation(MongoClient client) {
        // 插入文档
        JsonObject book1 = new JsonObject()
                .put("title", "math")
                .put("_id", "2");
        client.insert("books", book1, res -> {
            if (res.succeeded()) {
                LOGGER.info("insert document successfully");
            } else {
                LOGGER.error("insert document failed");
            }
        });
        // 当_id已存在时插入会失败
        JsonObject book2 = new JsonObject()
                .put("title", "chinese")
                .put("_id", "2");
        client.insert("books", book2, res -> {
            if (res.succeeded()) {
                LOGGER.info("insert document successfully ");
            } else {
                LOGGER.error("insert document failed");
            }
        });
    }

    public static void updateOperation(MongoClient client) {
        // 插入几条title都相同的数据
//        for(int i=0;i<5;i++){
//            JsonObject book = new JsonObject()
//                    .put("title", "netty in action");
//            client.save("books", book, res -> {
//                if (res.succeeded()) {
//                    LOGGER.info("save document successfully");
//                } else {
//                    LOGGER.error("save document failed");
//                }
//            });
//        }

        // 创建查询条件
        JsonObject query = new JsonObject()
                .put("title", "netty in action");
        // 要set的属性值
        JsonObject update = new JsonObject()
                .put("$set", new JsonObject()
                        .put("title", "design pattern")
                        .put("author", "Mr Yao")
                );
        // 更新文档，只会匹配一条且更新一条
        client.updateCollection("books", query, update, res -> {
            if (res.succeeded()) {
                long matched = res.result().getDocMatched();
                long modified = res.result().getDocModified();
                LOGGER.info("update document successfully,matched {} documents,modified {} documents.", matched, modified);
            } else {
                LOGGER.error("update document failed");
            }
        });

        // 设置更新选项
        UpdateOptions options = new UpdateOptions()
                // 是否匹配更新多条
                .setMulti(true)
                // 未匹配到查询条件，是否插入一条记录
                .setUpsert(true);
        client.updateCollectionWithOptions("books", query, update, options, res -> {
            if (res.succeeded()) {
                long matched = res.result().getDocMatched();
                long modified = res.result().getDocModified();
                LOGGER.info("updateWithOption document successfully,matched {} documents,modified {} documents.", matched, modified);
            } else {
                LOGGER.error("updateWithOption document failed");
            }
        });
    }

    public static void replaceOperation(MongoClient client) {
        // 创建查询条件
        JsonObject query = new JsonObject()
                .put("title", "chinese");
        // 创建替换的对象
        JsonObject replacement = new JsonObject()
                .put("title", "effective java")
                .put("author", "wangwu");

        client.replaceDocuments("books", query, replacement, res -> {
            if (res.succeeded()) {
                LOGGER.info("replace document successfully");
            } else {
                LOGGER.error("replace document failed");
            }
        });
    }

    public static void findOperation(MongoClient client) {
        // 创建查询条件  如果不加任何属性，则默认查询所有的记录 类似于
        JsonObject query = new JsonObject()
                .put("author", "Mr Yao");
        FindOptions option = new FindOptions()
                // 要查询的字段，默认为null 查询所有字段
                // json的key是字段，value随便
                .setFields(new JsonObject()
                        .put("title", 1)
                        .put("author", 2))
                // 设置取得大小  默认-1 取所有的
                .setLimit(5);
        client.findWithOptions("books", query, option, res -> {
            if (res.succeeded()) {
                LOGGER.info("find documents successfully");
                res.result().forEach(item -> {
                    LOGGER.info("document is {}", item.encodePrettily());
                });
            } else {
                LOGGER.error("find documents failed");
            }
        });
    }
}
