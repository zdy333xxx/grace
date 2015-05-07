/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zdy.base.controller;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.zdy.base.conf.MongoConfig;
import com.zdy.base.service.PictureService;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author breeze
 */
@Controller
@RequestMapping("/picture")
public class PictureController {

    @Autowired
    private MongoConfig MongoConfig;

    @Autowired
    private PictureService pictureService;

    @RequestMapping("/test")
    protected void handPictureRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");

        //MongoCredential credential = MongoCredential.createMongoCRCredential(MongoConfig.getUsername(), MongoConfig.getDatabase(), MongoConfig.getPassword().toCharArray());
        MongoClient mongoClient = new MongoClient(new ServerAddress(MongoConfig.getHost(), MongoConfig.getPort()));//, Arrays.asList(credential));

        DB db = mongoClient.getDB(MongoConfig.getDatabase());

        byte[] picture = pictureService.service(db);

        mongoClient.close();

        response.setContentType("image/jpeg");
        response.getOutputStream().write(picture);
    }

}
