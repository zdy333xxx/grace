/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zdy.base.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 *
 * @author breeze
 */
@Service
@Scope("prototype")
public class PictureService {
    
    public byte[] service(DB db){
        
        DBCollection coll =db.getCollection("mytest");
        
        DBObject query =new BasicDBObject();
        DBObject fields =new BasicDBObject("ZP",1);
        
        DBObject doc =coll.findOne(query,fields);
        
        return (byte[]) doc.get("ZP");
    }
    
}
