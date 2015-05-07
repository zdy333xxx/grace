/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import bolts.MongoWritter;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import java.net.UnknownHostException;
import java.util.Date;
import spouts.MongoReader;

public class MongoTopologyMain {

    public static void main(String[] args) throws InterruptedException, UnknownHostException {

        Integer ReaderCount = 1;
        Integer WritterCount = 3;
        //CustomStatus.setTaskCount(taskCount);

        //定义拓扑
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("mongo-reader", new MongoReader(), ReaderCount);
        builder.setBolt("mongo-copyer", new MongoWritter(), WritterCount).shuffleGrouping("mongo-reader");
        //builder.setBolt("word-counter", new WordCounter(), 2).fieldsGrouping("word-normalizer", new Fields("word"));

        //配置
        Config conf = new Config();
        //源集合
        conf.put("source_host", "localhost");
        conf.put("source_port", 40002);
        conf.put("source_database", "rtdb");
        conf.put("source_username", "hzga");
        conf.put("source_password", "workhzga1234");
        conf.put("source_collection", "query_person_photo");   //query_person_photo query_qgrk_backup

        //目标集合
        conf.put("target_host", "localhost");
        conf.put("target_port", 27017);
        conf.put("target_database", "rtdb");
        conf.put("target_username", null);
        conf.put("target_password", null);
        conf.put("target_collection", "query_person_photo");

        //设置调试开关
        conf.setDebug(false);

        //运行拓扑
        conf.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("Getting-Started-Topologie", conf, builder.createTopology());

        long start = new Date().getTime();

        MongoClient mongoClient = new MongoClient(new ServerAddress("localhost", 27017));//, Arrays.asList(credential));

        DB db = mongoClient.getDB("rtdb");
        DBCollection coll = db.getCollection("query_person_photo");  //query_person_photo query_qgrk_backup

        while (coll.count() < 88594) {
            Thread.sleep(1000 * 5 * 1);
        }
        mongoClient.close();

        long end = new Date().getTime();
        System.out.println("总耗时------------" + ": " + (double) (end - start) / 1000 + " S");

        Thread.sleep(1000 * 5);

        System.out.println("集群已关闭....................................");
        cluster.shutdown();

    }
}
