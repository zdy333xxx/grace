/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spouts;

import java.util.Map;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import com.mongodb.BasicDBObject;
import com.mongodb.Bytes;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MongoReader implements IRichSpout {

    Map conf;
    private SpoutOutputCollector collector;
    private boolean completed = false;
    private TopologyContext context;

    //long start;
    @Override
    public boolean isDistributed() {
        return false;
    }

    @Override
    public void ack(Object msgId) {
        System.out.println("OK:" + msgId);
    }

    @Override
    public void close() {

        //System.out.println(this.getClass().getName() + "----------------closed");
    }

    @Override
    public void fail(Object msgId) {
        System.out.println("FAIL:" + msgId);
    }

    /**
     * 这个方法做的惟一一件事情就是分发文件中的文本行
     */
    @Override
    public void nextTuple() {
        /**
         * 这个方法会不断的被调用，直到整个文件都读完了，我们将等待并返回。
         */
        if (completed) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                //什么也不做
            }
            //CustomStatus.setCompleted(completed);
            return;
        }

        MongoClient mongoClient = null;

        //创建reader
        try {

            String host = (String) conf.get("source_host");
            int port = Integer.parseInt(conf.get("source_port").toString());
            String database = (String) conf.get("source_database");
            String username = (String) conf.get("source_username");
            String password = (String) conf.get("source_password");
            String collection = (String) conf.get("source_collection");

            if (username != null && !username.trim().isEmpty() && password != null && !password.trim().isEmpty()) {
                MongoCredential credential = MongoCredential.createMongoCRCredential(username, database, password.toCharArray());
                mongoClient = new MongoClient(new ServerAddress(host, port), Arrays.asList(credential));
            } else {
                mongoClient = new MongoClient(new ServerAddress(host, port));
            }

            DB db = mongoClient.getDB(database);
            DBCollection coll = db.getCollection(collection);

            DBObject fields = new BasicDBObject();
            fields.put("_id", 0);
            DBCursor cursor = coll.find(new BasicDBObject(), fields);
            cursor.addOption(Bytes.QUERYOPTION_NOTIMEOUT);

            List<Map> docMapList = new ArrayList<Map>();

            //读所有文本行
            while (cursor.hasNext()) {
                /**
                 * 按行发布一个新值
                 */
                docMapList.add(cursor.next().toMap());

                if (docMapList.size() % 1000 == 0) {
                    this.collector.emit(new Values(docMapList));
                    docMapList = new ArrayList<Map>();
                }
            }
            cursor.close();

            this.collector.emit(new Values(docMapList));

        } catch (Exception e) {
            throw new RuntimeException("Error reading tuple", e);
        } finally {
            if (null != mongoClient) {
                mongoClient.close();
            }
            completed = true;
        }

    }

    /**
     * 我们将创建一个文件并维持一个collector对象
     *
     * @param conf
     * @param context
     * @param collector
     */
    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.conf = conf;
        this.context = context;
        this.collector = collector;
    }

    /**
     * 声明输入域"word"
     *
     * @param declarer
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("line"));
    }
}
