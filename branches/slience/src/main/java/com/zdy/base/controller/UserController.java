/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zdy.base.controller;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.zdy.base.conf.MongoConfig;
import com.zdy.base.pojo.User;
import java.util.Date;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author breeze
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MongoConfig mongoConfig;

    @RequestMapping("/login")
    protected void handLoginRequest(HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody User userModel) throws Exception {
        
        long start =new Date().getTime();
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        ObjectNode resultNode = JsonNodeFactory.instance.objectNode();
        resultNode.put("resultCode", 0);
        resultNode.put("resultMsg", "登录失败");


        String username = userModel.getUsername();
        String password = userModel.getPassword();
        
        System.out.println(username+"---"+password);

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            response.getWriter().write(resultNode.toString());
            return;
        }

        // MongoCredential credential = MongoCredential.createMongoCRCredential(mongoConnModel.getUsername(), mongoConnModel.getDatabase(), mongoConnModel.getPassword().toCharArray());
        MongoClient mongoClient = new MongoClient(new ServerAddress(mongoConfig.getHost(), mongoConfig.getPort()));//, Arrays.asList(credential));

        DB db = mongoClient.getDB(mongoConfig.getDatabase());

        DBCollection coll = db.getCollection("slience_user");

        DBObject query = new BasicDBObject();
        query.put("username", username);
        //query.put("password", password);
        //query.put("enable", true);
        
        DBObject fieldDoc = new BasicDBObject();
        fieldDoc.put("password", 1);
        fieldDoc.put("enable", 1);

        DBObject doc = coll.findOne(query,fieldDoc);

        mongoClient.close();

        System.out.println(doc);
        
        if (password.equals((String)doc.get("password"))&&(Boolean)doc.get("enable")) {
            resultNode.put("resultCode", 1);
            resultNode.put("resultMsg", "登录成功");
        }
        
        long end =new Date().getTime();
        
        System.out.println("费时-->"+(double)(end-start)/1000+" S");
        
        System.out.println(new HashMap<Integer,Object>(Math.max((int) (6/.75f) + 1, 16)));

        response.getWriter().write(resultNode.toString());
    }
}
