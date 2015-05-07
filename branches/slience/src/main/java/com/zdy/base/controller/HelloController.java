/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zdy.base.controller;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.zdy.base.conf.MongoConfig;
import com.zdy.base.service.HelloService;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author breeze
 */
@Controller
@RequestMapping("/hello")
public class HelloController {

    @Autowired
    private MongoConfig MongoConfig;

    @Autowired
    private HelloService helloService;

    @RequestMapping("/list")
    @ResponseBody
    protected Set<?> handHelloRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");

        //MongoCredential credential = MongoCredential.createMongoCRCredential(MongoConfig.getUsername(), MongoConfig.getDatabase(), MongoConfig.getPassword().toCharArray());
        //MongoClient mongoClient = new MongoClient(new ServerAddress(MongoConfig.getHost(), MongoConfig.getPort()));//, Arrays.asList(credential));

        //DB db = mongoClient.getDB(MongoConfig.getDatabase());

        //Set<String> collNameSet = db.getCollectionNames();

        //mongoClient.close();

        //return collNameSet;
        return System.getProperties().entrySet();
    }

    

}
