/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zdy.base.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 *
 * @author breeze
 */
@Service
@Scope("prototype")
public class HelloService {

    public String service(DB db) {

        DBObject query = new BasicDBObject();
        DBObject fields = new BasicDBObject("XM", 1);

        String resultStr = db.getCollection("gazhk_CZRK").find(query, fields).limit(3).toArray().toString();

        System.out.print(resultStr);

        return resultStr;
    }

}
