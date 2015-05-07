/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bolts;

import java.util.HashMap;
import java.util.Map;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoWritter implements IRichBolt {

    Map conf;
    Integer id;
    String name;
    Map<String, Integer> counters;
    private OutputCollector collector;
    MongoClient mongoClient;
    DBCollection coll;
    int num;

    /**
     * 这个spout结束时（集群关闭的时候），我们会显示单词数量
     */
    @Override
    public void cleanup() {
        if (null != mongoClient) {
            mongoClient.close();
        }

        System.out.println("-- 复制结束 【" + name + "-" + id + "】 --" + num + " 条记录");

    }

    /**
     * 为每个单词计数
     *
     * @param input
     */
    @Override
    public void execute(Tuple input) {
        //DBObject doc = (DBObject) input.getValue(0);
        System.out.println("------------------------------------------");

        /**
         * 如果单词尚不存在于map，我们就创建一个，如果已在，我们就为它加1
         */
        List<Map> docMapList = (List<Map>) input.getValue(0);

        List<DBObject> docList = new ArrayList<DBObject>();
        for (Map docMap : docMapList) {
            docList.add(new BasicDBObject(docMap));
        }

        coll.insert(docList, WriteConcern.SAFE);

        int insertSize = docList.size();

        num += insertSize;
        System.out.println("-- 复制 【" + name + "-" + id + "】 --" + num + " 条记录");

        //对元组做出应答
        collector.ack(input);
    }

    /**
     * 初始化
     *
     * @param conf
     * @param context
     * @param collector
     */
    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        this.counters = new HashMap<String, Integer>();
        this.collector = collector;
        this.name = context.getThisComponentId();
        this.id = context.getThisTaskId();

        try {
            String host = (String) conf.get("target_host");
            int port = Integer.parseInt(conf.get("target_port").toString());
            String database = (String) conf.get("target_database");
            String username = (String) conf.get("target_username");
            String password = (String) conf.get("target_password");
            String collection = (String) conf.get("target_collection");

            if (username != null && !username.trim().isEmpty() && password != null && !password.trim().isEmpty()) {
                MongoCredential credential = MongoCredential.createMongoCRCredential(username, database, password.toCharArray());
                mongoClient = new MongoClient(new ServerAddress(host, port), Arrays.asList(credential));
            } else {
                mongoClient = new MongoClient(new ServerAddress(host, port));
            }

            DB db = mongoClient.getDB(database);
            coll = db.getCollection(collection);
        } catch (UnknownHostException ex) {
            if (mongoClient != null) {
                mongoClient.close();
            }

            Logger.getLogger(MongoWritter.class.getName()).log(Level.SEVERE, null, ex);
            cleanup();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }
}
