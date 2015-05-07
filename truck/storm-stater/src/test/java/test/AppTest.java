/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zdy
 */
public class AppTest {

    public static void main(String[] args) {

        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put("host", "localhost");
        conf.put("port", 30001);
        conf.put("database", "work");
        conf.put("username", "work");
        conf.put("password", "work001");

        try {
            int N = 0;

            MongoCredential credential = MongoCredential.createMongoCRCredential((String) conf.get("username"), (String) conf.get("database"), ((String) conf.get("password")).toCharArray());
            MongoClient mongoClient = new MongoClient(new ServerAddress((String) conf.get("host"), (Integer) conf.get("port")), Arrays.asList(credential));

            DB db = mongoClient.getDB((String) conf.get("database"));
            DBCollection coll = db.getCollection("storm_in_2");

            List<DBObject> list = new ArrayList<DBObject>();

            int n = 0;
            while (N < 50) {

                DBObject doc = new BasicDBObject();
                doc.put("index", N++);
                doc.put("name", "赵东阳_" + N);
                doc.put("sex", "男");
                list.add(doc);
                n++;
                if (n == 1000) {
                    coll.insert(list);
                    list.clear();
                    n = 0;
                    System.out.println("N" + ": " + N);
                }
            }
            coll.insert(list);

            mongoClient.close();
        } catch (UnknownHostException ex) {
            Logger.getLogger(AppTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
