package com.hzcominfo.aggr.util;

import com.mongodb.BasicDBObject;
import com.mongodb.Bytes;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author xxx
 */
public class MongoOpt {
    
    public static DBObject findOne(DB db, String collName, DBObject query, DBObject fieldDoc) {
        DBObject obj = null;

        if (db == null || collName == null || collName.trim().isEmpty()) {
            return obj;
        }

        DBCollection coll = db.getCollection(collName);

        DBCursor cursor = coll.find(query,fieldDoc);
        cursor.addOption(Bytes.QUERYOPTION_NOTIMEOUT);

        while (cursor.hasNext()) {
            DBObject doc = cursor.next();
            if (doc == null) {
                continue;
            }

            obj = doc;
            break;
        }
        cursor.close();

        return obj;
    }

    public static DBObject findOne(DB db, String collName, DBObject query,DBObject fieldDoc, DBObject sort) {
        DBObject obj = null;

        if (db == null || collName == null || collName.trim().isEmpty()) {
            return obj;
        }

        DBCollection coll = db.getCollection(collName);

        DBCursor cursor = coll.find(query,fieldDoc).sort(sort);
        cursor.addOption(Bytes.QUERYOPTION_NOTIMEOUT);

        while (cursor.hasNext()) {
            DBObject doc = cursor.next();
            if (doc == null) {
                continue;
            }

            obj = doc;
            break;
        }
        cursor.close();

        return obj;
    }

    public static List<DBObject> find(DB db, String collName, DBObject query,DBObject fieldDoc, DBObject sort) {

        List<DBObject> docList = new ArrayList<DBObject>();
        if (db == null || collName == null || collName.trim().isEmpty()) {
            return docList;
        }

        DBCollection coll = db.getCollection(collName);

        DBCursor cursor = coll.find(query,fieldDoc).sort(sort);
        cursor.addOption(Bytes.QUERYOPTION_NOTIMEOUT);

        while (cursor.hasNext()) {
            DBObject doc = cursor.next();
            if (doc == null) {
                continue;
            }

            docList.add(doc);
        }
        cursor.close();

        return docList;
    }

    public static List<DBObject> find(DB db, String collName, DBObject query,DBObject fieldDoc, DBObject sort, int nLimit) {

        List<DBObject> docList = new ArrayList<DBObject>();
        if (db == null || collName == null || collName.trim().isEmpty()) {
            return docList;
        }

        DBCollection coll = db.getCollection(collName);

        DBCursor cursor = coll.find(query,fieldDoc).sort(sort).limit(nLimit);
        cursor.addOption(Bytes.QUERYOPTION_NOTIMEOUT);

        while (cursor.hasNext()) {
            DBObject doc = cursor.next();
            if (doc == null) {
                continue;
            }

            docList.add(doc);
        }
        cursor.close();

        return docList;
    }

    public static List<DBObject> find(DB db, String collName, DBObject query,DBObject fieldDoc, DBObject sort, int nSkip, int nLimit) {

        List<DBObject> docList = new ArrayList<DBObject>();
        if (db == null || collName == null || collName.trim().isEmpty()) {
            return docList;
        }

        DBCollection coll = db.getCollection(collName);

        DBCursor cursor = coll.find(query,fieldDoc).sort(sort).skip(nSkip).limit(nLimit);
        cursor.addOption(Bytes.QUERYOPTION_NOTIMEOUT);

        while (cursor.hasNext()) {
            DBObject doc = cursor.next();
            if (doc == null) {
                continue;
            }

            docList.add(doc);
        }
        cursor.close();

        return docList;
    }

    public static boolean insert(DB db, String collName, DBObject docParam) {
        boolean b = false;
        if (db == null || collName == null || collName.trim().isEmpty()) {
            return b;
        }

        DBCollection coll = db.getCollection(collName);

        WriteResult wr = coll.insert(docParam);

        if (wr.getLastError().getDouble("ok") > 0) {
            b = true;
        }

        return b;
    }

    public static boolean insertMulti(DB db, String collName, List<DBObject> docListParam) {
        boolean b = false;
        if (db == null || collName == null || collName.trim().isEmpty()) {
            return b;
        }

        DBCollection coll = db.getCollection(collName);

        WriteResult wr = coll.insert(docListParam);

        if (wr.getLastError().getDouble("ok") > 0) {
            b = true;
        }

        return b;
    }

    public static boolean update(DB db, String collName, DBObject query, DBObject docParam) {
        boolean b = false;
        if (db == null || collName == null || collName.trim().isEmpty()) {
            return b;
        }

        DBCollection coll = db.getCollection(collName);

        DBObject object = new BasicDBObject();
        object.put("$set", docParam);

        WriteResult wr = coll.update(query, object);

        if (wr.getLastError().getDouble("ok") > 0) {
            b = true;
        }

        return b;
    }

    public static boolean updateMulti(DB db, String collName, DBObject query, DBObject docParam) {
        boolean b = false;
        if (db == null || collName == null || collName.trim().isEmpty()) {
            return b;
        }

        DBCollection coll = db.getCollection(collName);

        DBObject object = new BasicDBObject();
        object.put("$set", docParam);

        WriteResult wr = coll.updateMulti(query, object);

        if (wr.getLastError().getDouble("ok") > 0) {
            b = true;
        }

        return b;
    }

    public static boolean delete(DB db, String collName, DBObject query) {
        boolean b = false;
        if (db == null || collName == null || collName.trim().isEmpty()) {
            return b;
        }

        DBCollection coll = db.getCollection(collName);

        WriteResult wr = coll.remove(query);

        if (wr.getLastError().getDouble("ok") > 0) {
            b = true;
        }

        return b;
    }
    
    
    public static long count(DB db, String collName, DBObject query) {

        if (db == null || collName == null || collName.trim().isEmpty()) {
            return 0;
        }

        DBCollection coll = db.getCollection(collName);
        
        return coll.count(query);
    }
}
