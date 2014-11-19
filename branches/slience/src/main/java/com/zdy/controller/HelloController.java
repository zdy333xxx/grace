/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zdy.controller;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.zdy.conf.MongoConnModel;
import com.zdy.service.HelloService;
import com.zdy.service.PictureService;
import java.util.Arrays;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author breeze
 */
@Controller
public class HelloController {

    @Autowired
    private MongoConnModel mongoConnModel;
    @Autowired
    private HelloService helloService;
    @Autowired
    private PictureService pictureService;

    @RequestMapping("/hello.htm")
    protected void handHelloRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");


        MongoCredential credential = MongoCredential.createMongoCRCredential(mongoConnModel.getUsername(), mongoConnModel.getDatabase(), mongoConnModel.getPassword().toCharArray());
        MongoClient mongoClient = new MongoClient(new ServerAddress(mongoConnModel.getHost(), mongoConnModel.getPort()), Arrays.asList(credential));

        DB db = mongoClient.getDB(mongoConnModel.getDatabase());


        Set<String> collNameSet =db.getCollectionNames();

        
        mongoClient.close();
        

        ArrayNode collNameArrayNode =JsonNodeFactory.instance.arrayNode();
        for(String collName:collNameSet){
            collNameArrayNode.add(collName);
        }

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(collNameArrayNode.toString());
    }

    @RequestMapping("/picture.htm")
    protected void handPictureRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");


        MongoCredential credential = MongoCredential.createMongoCRCredential(mongoConnModel.getUsername(), mongoConnModel.getDatabase(), mongoConnModel.getPassword().toCharArray());
        MongoClient mongoClient = new MongoClient(new ServerAddress(mongoConnModel.getHost(), mongoConnModel.getPort()), Arrays.asList(credential));

        DB db = mongoClient.getDB(mongoConnModel.getDatabase());


        byte[] picture = getPictureService().service(db);


        mongoClient.close();


        response.setContentType("image/jpeg");
        response.getOutputStream().write(picture);
    }

    @RequestMapping("/test.htm")
    protected void handTestRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");


        MongoCredential credential = MongoCredential.createMongoCRCredential(mongoConnModel.getUsername(), mongoConnModel.getDatabase(), mongoConnModel.getPassword().toCharArray());
        MongoClient mongoClient = new MongoClient(new ServerAddress(mongoConnModel.getHost(), mongoConnModel.getPort()), Arrays.asList(credential));

        DB db = mongoClient.getDB(mongoConnModel.getDatabase());


        String resultStr = getHelloService().service(db);


        mongoClient.close();


        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(resultStr);
    }

    /**
     * @return the mongoConnModel
     */
    public MongoConnModel getMongoConnModel() {
        return mongoConnModel;
    }

    /**
     * @param mongoConnModel the mongoConnModel to set
     */
    public void setMongoConnModel(MongoConnModel mongoConnModel) {
        this.mongoConnModel = mongoConnModel;
    }

    /**
     * @return the helloService
     */
    public HelloService getHelloService() {
        return helloService;
    }

    /**
     * @param helloService the helloService to set
     */
    public void setHelloService(HelloService helloService) {
        this.helloService = helloService;
    }

    /**
     * @return the pictureService
     */
    public PictureService getPictureService() {
        return pictureService;
    }

    /**
     * @param pictureService the pictureService to set
     */
    public void setPictureService(PictureService pictureService) {
        this.pictureService = pictureService;
    }
}
