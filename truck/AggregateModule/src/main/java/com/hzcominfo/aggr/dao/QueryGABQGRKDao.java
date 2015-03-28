/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hzcominfo.aggr.dao;

import com.hzcominfo.mongoutil.MongoUtil;
import com.hzcominfo.querygabqgrk.service.QueryGABQGRKService;
import com.hzcominfo.aggr.util.MongoOpt;
import com.hzcominfo.auth.model.AuthUserContext;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author breeze
 */
public class QueryGABQGRKDao {

    private static final String qgrkBaseInfoCollName = "query_qgrk_backup";
    private static final String mongoPhotoCollName = "query_person_photo";
    private final static Map<String, String> fieldNameCodeMap = new HashMap<String, String>();//全国人口库字段代码值翻译时对应的字段名映射


    static {
        /*
         fieldNameMap.put("SFZH", "身份证号");
         fieldNameMap.put("RYBH", "人员编号");
         fieldNameMap.put("XM", "姓名");
         fieldNameMap.put("CYM", "曾用名");
         fieldNameMap.put("XB", "性别");
         fieldNameMap.put("MZ", "民族");
         fieldNameMap.put("HYZK", "婚姻状况");
         fieldNameMap.put("WHCD", "文化程度");
         fieldNameMap.put("CSRQ", "出生日期");
         fieldNameMap.put("CSD", "出生地");
         fieldNameMap.put("JGSSX", "籍贯");
         fieldNameMap.put("HKSZD", "户籍地");
         fieldNameMap.put("FWCS", "服务场所");
         fieldNameMap.put("XP", "相片");
         */
        // ----------------------------------
        fieldNameCodeMap.put("XB", "XB");
        fieldNameCodeMap.put("MZ", "MZ");
        fieldNameCodeMap.put("HYZK", "HYZK");
        fieldNameCodeMap.put("WHCD", "WHCD");
        fieldNameCodeMap.put("JGSSX", "XZQH");
        fieldNameCodeMap.put("HKSZD", "XZQH");
        fieldNameCodeMap.put("CSD", "XZQH");
    }

    /*
     * 在全国人口库中查找人员信息
     */
    public static Map<String, Map<String, String>> queryPersonInfo(AuthUserContext modelContext, DB db, Map<String, String> paramMap, //查询参数
            Map<String, Map<String, String>> attrMap, //查询字段清单
            String baseAttrColName) {

        String userCardId = modelContext.getUsers().getPkiSfzh();//用户身份证号
        String userName = modelContext.getUsers().getPkiName(); //用户名
        String userDept = modelContext.getUsers().getUserDeptCode();//用户单位

        Map<String, String> resultMap = QueryGABQGRKService.queryPersonInfo(paramMap, userCardId, userName, userDept);

        if (resultMap == null) {
            return null;
        }

        boolean notEmptyFlag = false;

        Set<String> attrNameSet = attrMap.keySet();
        for (String attrName : attrNameSet) {
            if (!resultMap.containsKey(attrName)) {
                continue;
            }
            String attrValue = resultMap.get(attrName);
            if (attrValue == null || attrValue.trim().isEmpty()) {
                continue;
            }
            notEmptyFlag = true;
            break;
        }

        if (notEmptyFlag != true) {
            return null;
        }

        for (String attrName : attrNameSet) {
            String attrValue = resultMap.get(attrName);
            if (attrValue == null || attrValue.trim().isEmpty()) {
                attrValue = "";
            } else if (fieldNameCodeMap.containsKey(attrName)) {
                attrValue = parseQGRKFieldCode(db, fieldNameCodeMap.get(attrName), attrValue); //对全国人口库中字段的代码值进行翻译       
            }

            Map<String, String> attrValueMap = attrMap.get(attrName);
            attrValueMap.put(baseAttrColName, attrValue);
        }

        byte[] photo = null;

        //将人员基本信息存入mongo数据库
        DBObject query = new BasicDBObject();

        Set<String> paramKeySet = paramMap.keySet();
        for (String key : paramKeySet) {
            query.put(key, paramMap.get(key));
        }

        DBObject fieldDoc = new BasicDBObject();

        Set<String> resultMapKeySet = resultMap.keySet();
        for (String keyName : resultMapKeySet) {
            fieldDoc.put(keyName, 1);
        }

        //检查人员信息在mongo数据库中是否已经存在，若已存在，便不再保存
        long count = MongoOpt.count(db, qgrkBaseInfoCollName, query);
        if (count < 1) {

            DBObject newPersonDoc = new BasicDBObject();

            //Set<String> resultMapKeySet = resultMap.keySet();
            for (String keyName : resultMapKeySet) {
                if (!"XP".equals(keyName)) {
                    String attrValue = resultMap.get(keyName);
                    if (attrValue == null) {
                        attrValue = "";
                    }
                    newPersonDoc.put(keyName, attrValue);
                    continue;
                }

                String XPStr = resultMap.get("XP");
                if (XPStr == null || XPStr.isEmpty()) {
                    continue;
                }

                //将人员照片信息存入mongo数据库
                photo = Base64.decodeBase64(XPStr);//(new BASE64Decoder()).decodeBuffer(XPStr);
                newPersonDoc.put("ZP", photo);
            }

            newPersonDoc.put("add_time", new Date());
            newPersonDoc.put("enable", 1);

            MongoOpt.insert(db, qgrkBaseInfoCollName, newPersonDoc);
        }

        //将人员照片信息存入mongo数据库
        if (photo != null) {

            String sfzh = paramMap.get("SFZH");
            if (sfzh != null && !sfzh.isEmpty()) {
                query = new BasicDBObject();
                query.put("SFZH", sfzh);

                count = MongoOpt.count(db, mongoPhotoCollName, query);
                if (count < 1) {

                    //将照片数据存入mongo数据库
                    DBObject newPhotoDoc = new BasicDBObject();
                    newPhotoDoc.put("SFZH", sfzh);
                    newPhotoDoc.put("ZP", photo);
                    newPhotoDoc.put("zp_source", "全国人口库");
                    newPhotoDoc.put("add_time", new Date());
                    newPhotoDoc.put("enable", 1);

                    MongoOpt.insert(db, mongoPhotoCollName, newPhotoDoc);
                }
            }
        }

        return attrMap;
    }

    /*
     * 在Mongo数据库中的全国人口信息备份库中查找人员信息
     */
    public static boolean queryPersonBaseInfoFromQGRKBackup(DB db, //Mongo数据库对象
            //String collName,                                                    //全国人口信息在Mongo数据库中的备份集合    
            Map<String, String> paramMapOfAttrSet, //查询参数
            Map<String, Map<String, String>> attrMap, //查询结果Map
            String baseAttrColName //基本属性对应的Mongo集合
    ) {

        DBObject query = new BasicDBObject();

        Set<String> paramKeySet = paramMapOfAttrSet.keySet();
        for (String key : paramKeySet) {
            query.put(key, paramMapOfAttrSet.get(key));
        }
        query.put("enable", 1);

        DBObject fieldDoc = new BasicDBObject();

        Set<String> attrNameSet = attrMap.keySet();
        for (String attrName : attrNameSet) {
            fieldDoc.put(attrName, 1);
        }

        DBObject personDoc = MongoOpt.findOne(db, qgrkBaseInfoCollName, query, fieldDoc);
        if (personDoc == null) {
            return false;
        }

        //Set<String> attrNameSet = attrMap.keySet();
        for (String attrName : attrNameSet) {
            /*
             if (!personDoc.containsField(attrName)) {
             continue;
             }
             */
            String attrValue = MongoUtil.getStringValue(personDoc, attrName);
            if (attrValue != null && !attrValue.trim().isEmpty() && fieldNameCodeMap.containsKey(attrName)) {
                attrValue = parseQGRKFieldCode(db, fieldNameCodeMap.get(attrName), attrValue); //对全国人口库中字段的代码值进行翻译       
            }

            Map<String, String> attrValueMap = attrMap.get(attrName);
            attrValueMap.put(baseAttrColName, attrValue);
        }

        return true;
    }

    public static String parseQGRKFieldCode(DB db, String fieldName, String code) {
        String valueStr = "";

        String codeCollName = "query_qgrk_code_map";

        DBObject query = new BasicDBObject();
        query.put("field_name", fieldName);
        query.put("code", code);

        DBObject fieldDoc = new BasicDBObject();
        fieldDoc.put("value", 1);

        DBObject doc = MongoOpt.findOne(db, codeCollName, query, fieldDoc);
        if (doc == null) {
            return valueStr;
        }
        valueStr = MongoUtil.getStringValue(doc, "value");

        return valueStr;
    }
}
