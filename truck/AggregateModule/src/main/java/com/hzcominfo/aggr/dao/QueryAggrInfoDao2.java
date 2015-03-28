/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hzcominfo.aggr.dao;

import com.hzcominfo.mongoutil.MongoUtil;
import com.hzcominfo.aggr.util.MongoOpt;
import com.hzcominfo.auth.model.AuthUserContext;
import com.mongodb.BasicDBObject;
import com.mongodb.Bytes;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

/**
 *
 * @author breeze
 */
public class QueryAggrInfoDao2 {

    private static final String QUERY_DIMENSION_DEFINE_COLL_NAME = "query_dimension_define";
    private static final String QUERY_ATTRSET_DEFINE_COLL_NAME = "query_attrset_define";
    private static final String QUERY_DIMENSION_ATTRSET_COLL_NAME = "query_dimension_attrset";
    private static final String QUERY_ATTRSET_PARAM_COLL_NAME = "query_attrset_param";
    private static final String QUERY_ATTR_DEFINE_COLL_NAME = "query_attr_define";
    private static final String QUERY_ATTR_EXTEND_PARAM_COLL_NAME = "query_attr_extend_param";
    //private static final String QUERY_COL_SYSTEM_MAP_COLL_NAME ="query_col_system_map";
    private static int maxRows = Integer.MAX_VALUE;
    private static double queryCollTotalSpendTime = 0;
    private static Map<String, List<DBObject>> queryCollResultCacheMap = new HashMap<String, List<DBObject>>();
    private static Map<String, Set<String>> queryCollFieldsCacheMap = new HashMap<String, Set<String>>();
    private static Map<String, Map<String, Object>> extraQueryParamOfAllCollMap = new HashMap<String, Map<String, Object>>();

    //获取字段列表*************************
    public static JsonNode getFields(DB dbConfig, String dimensionParam) {
        ObjectNode rootNode = JsonNodeFactory.instance.objectNode();


        DBObject query = new BasicDBObject();
        query.put("query_dimension_name", dimensionParam);

        DBObject fieldDoc = new BasicDBObject();
        fieldDoc.put("query_dimension_display_name", 1);

        DBObject dimensionDoc = MongoOpt.findOne(dbConfig, QUERY_DIMENSION_DEFINE_COLL_NAME, query, fieldDoc);
        if (dimensionDoc == null) {
            return rootNode;
        }
        String dimensionDisplayName = (String) dimensionDoc.get("query_dimension_display_name"); //维度显示名称

        //对每个集合查询时的扩展（条件过滤）参数缓存

        query = new BasicDBObject();
        query.put("enable", 1);

        fieldDoc = new BasicDBObject();
        fieldDoc.put("enable", 0);

        DBObject sort = new BasicDBObject();
        sort.put("coll_name", 1);

        List<DBObject> extendParamDocList = MongoOpt.find(dbConfig, QUERY_ATTR_EXTEND_PARAM_COLL_NAME, query, fieldDoc, sort);
        
        for (DBObject extendParamDoc : extendParamDocList) {
            String collName = (String) extendParamDoc.get("coll_name");
            if (collName == null || collName.trim().isEmpty()) {
                continue;
            }
            String paramName = (String) extendParamDoc.get("param_name");
            if (paramName == null || paramName.trim().isEmpty()) {
                continue;
            }
            Object paramValue = extendParamDoc.get("param_value");

            Map<String, Object> extraQueryParamOfCollMap = extraQueryParamOfAllCollMap.get(collName);
            if (extraQueryParamOfCollMap == null) {
                extraQueryParamOfCollMap = new HashMap<String, Object>();
            }
            extraQueryParamOfCollMap.put(paramName, paramValue);
            
            extraQueryParamOfAllCollMap.put(collName, extraQueryParamOfCollMap);
        }

        System.out.println("extraQueryParamOfAllCollMap-->"+extraQueryParamOfAllCollMap);

        JsonNode attrSetNode = getFieldsOfAttrSetByDimension(dbConfig, dimensionParam);

        rootNode.put("dimensionCode", dimensionParam);
        rootNode.put("dimensionName", dimensionDisplayName);
        rootNode.put("attrSet", attrSetNode);

        return rootNode;
    }

    private static JsonNode getFieldsOfAttrSetByDimension(DB dbConfig, String dimensionParam) {
        ArrayNode rootNode = JsonNodeFactory.instance.arrayNode();

        DBObject query = new BasicDBObject();
        query.put("query_dimension_name", dimensionParam);
        query.put("aggr_flag", 0);
        query.put("enable_flag", 1);

        DBObject sort = new BasicDBObject();
        sort.put("display_order", 1);

        DBObject fieldDoc = new BasicDBObject();
        fieldDoc.put("query_attrset_id", 1);
        fieldDoc.put("query_attrset_name", 1);
        fieldDoc.put("query_attrset_display_name", 1);
        fieldDoc.put("display_order", 1);

        List<DBObject> attrSetList = MongoOpt.find(dbConfig, QUERY_DIMENSION_ATTRSET_COLL_NAME, query, fieldDoc, sort);

        //查询非聚合属性集的字段信息
        for (DBObject attrSetDoc : attrSetList) {

            long queryAttrSetId = MongoUtil.getLongValue(attrSetDoc, "query_attrset_id");//查询属性集编号

            String queryAttrSetName = MongoUtil.getStringValue(attrSetDoc, "query_attrset_name");//属性集合名称
            if (queryAttrSetName == null || queryAttrSetName.trim().isEmpty()) {
                continue;
            }

            String queryAttrSetDisplayName = MongoUtil.getStringValue(attrSetDoc, "query_attrset_display_name");//属性集显示名称
            if (queryAttrSetDisplayName == null || queryAttrSetDisplayName.trim().isEmpty()) {
                continue;
            }

            JsonNode attrNode = getFieldsOfAttrByAttrSet(dbConfig, queryAttrSetId); //查询当前属性集的下属属性

            ObjectNode attrSetNode = JsonNodeFactory.instance.objectNode();
            attrSetNode.put("attrSetCode", queryAttrSetName);
            attrSetNode.put("attrSetName", queryAttrSetDisplayName);
            attrSetNode.put("attr", attrNode);

            rootNode.add(attrSetNode);
        }

        //查询聚合属性集的字段信息
        query = new BasicDBObject();
        query.put("query_dimension_name", dimensionParam);
        query.put("aggr_flag", 1);
        query.put("enable_flag", 1);

        fieldDoc = new BasicDBObject();
        fieldDoc.put("query_attrset_name", 1);
        fieldDoc.put("query_attrset_display_name", 1);
        fieldDoc.put("display_order", 1);

        List<DBObject> aggrAttrSetList = MongoOpt.find(dbConfig, QUERY_DIMENSION_ATTRSET_COLL_NAME, query, fieldDoc, sort);

        //对聚合属性集进行二次查询
        for (DBObject aggrAttrSetDoc : aggrAttrSetList) {

            String aggrAttrSetName = MongoUtil.getStringValue(aggrAttrSetDoc, "query_attrset_name");//属性集合名称
            if (aggrAttrSetName == null || aggrAttrSetName.trim().isEmpty()) {
            }

            String aggrAttrSetDisplayName = MongoUtil.getStringValue(aggrAttrSetDoc, "query_attrset_display_name");//属性集显示名称
            if (aggrAttrSetDisplayName == null || aggrAttrSetDisplayName.trim().isEmpty()) {
            }

            query = new BasicDBObject();
            query.put("query_dimension_name", aggrAttrSetName);
            query.put("aggr_flag", 0);
            query.put("enable_flag", 1);

            fieldDoc = new BasicDBObject();
            fieldDoc.put("query_attrset_id", 1);
            fieldDoc.put("query_attrset_name", 1);
            fieldDoc.put("query_attrset_display_name", 1);
            fieldDoc.put("display_order", 1);

            //------------------------------------------    
            List<DBObject> aggrItemAttrSetList = MongoOpt.find(dbConfig, QUERY_DIMENSION_ATTRSET_COLL_NAME, query, fieldDoc, sort);

            ArrayNode aggrAttrNode = JsonNodeFactory.instance.arrayNode();

            //查询非聚合属性集的字段信息
            for (DBObject attrSetDoc : aggrItemAttrSetList) {

                long queryAttrSetId = MongoUtil.getLongValue(attrSetDoc, "query_attrset_id"); //查询属性集编号

                String queryAttrSetName = MongoUtil.getStringValue(attrSetDoc, "query_attrset_name");//查询属性集编号
                if (queryAttrSetName == null || queryAttrSetName.trim().isEmpty()) {
                    continue;
                }

                String queryAttrSetDisplayName = MongoUtil.getStringValue(attrSetDoc, "query_attrset_display_name");//属性集显示名称
                if (queryAttrSetDisplayName == null || queryAttrSetDisplayName.trim().isEmpty()) {
                    continue;
                }

                JsonNode attrNode = getFieldsOfAttrByAttrSet(dbConfig, queryAttrSetId); //查询当前属性集的下属属性

                ObjectNode attrSetNode = JsonNodeFactory.instance.objectNode();
                attrSetNode.put("attrSetCode", queryAttrSetName);
                attrSetNode.put("attrSetName", queryAttrSetDisplayName);
                attrSetNode.put("attr", attrNode);

                aggrAttrNode.add(attrSetNode);
            }

            ObjectNode AggeAttrSetNode = JsonNodeFactory.instance.objectNode();
            AggeAttrSetNode.put("attrSetCode", aggrAttrSetName);
            AggeAttrSetNode.put("attrSetName", aggrAttrSetDisplayName);
            AggeAttrSetNode.put("attr", aggrAttrNode);
            //-------------------------------------------
            rootNode.add(AggeAttrSetNode);
        }
        //
        return rootNode;
    }

    private static JsonNode getFieldsOfAttrByAttrSet(DB dbConfig, long attrSetId) {
        ArrayNode rootNode = JsonNodeFactory.instance.arrayNode();


        DBObject query = new BasicDBObject();
        query.put("query_attrset_id", attrSetId);
        //query.put("query_attr_owner_id", 0);

        DBObject sort = new BasicDBObject();
        sort.put("field_order", 1);

        DBObject fieldDoc = new BasicDBObject();
        fieldDoc.put("query_attrset_id", 0);
        fieldDoc.put("display_source_system", 0);
        fieldDoc.put("related_col_name", 0);
        fieldDoc.put("related_field_name", 0);


        List<DBObject> attrsOfAttrSetList = MongoOpt.find(dbConfig, QUERY_ATTR_DEFINE_COLL_NAME, query, fieldDoc, sort);


        List<DBObject> itemAttrList = new ArrayList<DBObject>();
        List<DBObject> arrayAttrList = new ArrayList<DBObject>();
        List<DBObject> groupAttrList = new ArrayList<DBObject>();
        Map<Long, List<DBObject>> itemAttrsMap = new HashMap<Long, List<DBObject>>();


        for (DBObject attrDoc : attrsOfAttrSetList) {

            long queryAttrOwnerId = MongoUtil.getIntValue(attrDoc, "query_attr_owner_id");
            if (queryAttrOwnerId != 0) {
                List<DBObject> itemAttrsList = itemAttrsMap.get(queryAttrOwnerId);

                if (itemAttrsList == null) {
                    itemAttrsList = new ArrayList<DBObject>();
                }
                itemAttrsList.add(attrDoc);
                itemAttrsMap.put(queryAttrOwnerId, itemAttrsList);

                continue;
            }


            int displayFlag = MongoUtil.getIntValue(attrDoc, "display_flag");
            if (displayFlag == 0) {
                continue;
            }

            String attrName = MongoUtil.getStringValue(attrDoc, "attr_name"); //字段名
            if (attrName == null || attrName.trim().isEmpty()) {
                continue;
            }

            int queryAttrItemType = MongoUtil.getIntValue(attrDoc, "query_attr_item_type");//属性的数据类别:1 单个元素 item, 2 多个元素 array     
            ////判断attr类型
            if (queryAttrItemType == 1) {
                String colFieldName = MongoUtil.getStringValue(attrDoc, "col_field_name"); //目标集合字段名
                if (colFieldName == null || colFieldName.trim().isEmpty()) {
                    continue;
                }

                //收集单字段
                DBObject newAttrDoc = new BasicDBObject();
                newAttrDoc.put("attr_name", attrName);
                newAttrDoc.put("attr_display_name", MongoUtil.getStringValue(attrDoc, "attr_display_name"));
                //newAttrDoc.put("field_order", MongoUtil.getIntValue(attrDoc, "field_order"));

                if (!itemAttrList.contains(newAttrDoc)) {
                    itemAttrList.add(newAttrDoc);
                }

            } else if (queryAttrItemType == 2) {
                arrayAttrList.add(attrDoc);
            } else if (queryAttrItemType == 3) {
                groupAttrList.add(attrDoc);
            }


        }



        //处理 itemAttr----------------------

        for (DBObject itemAttrDoc : itemAttrList) {
            ObjectNode attrNode = JsonNodeFactory.instance.objectNode();

            attrNode.put("attrCode", MongoUtil.getStringValue(itemAttrDoc, "attr_name"));
            attrNode.put("attrName", MongoUtil.getStringValue(itemAttrDoc, "attr_display_name"));
            attrNode.put("itemType", "item");//item表示单值，array表示多值且多字段

            rootNode.add(attrNode);
        }



        //处理 arrayAttr-----------------------------------

        for (DBObject arrayAttrDoc : arrayAttrList) {

            long queryAttrDefId = MongoUtil.getLongValue(arrayAttrDoc, "query_attr_def_id"); //属性的编号
            String colName = MongoUtil.getStringValue(arrayAttrDoc, "col_name");


            Set<String> fieldsSet = queryCollFieldsCacheMap.get(colName);
            if (fieldsSet == null) {
                fieldsSet = new HashSet<String>();
            }


            /*
             query = new BasicDBObject();
             query.put("query_attrset_id", attrSetId);
             query.put("query_attr_owner_id", queryAttrDefId);
             query.put("query_attr_item_type", 1);

             List<DBObject> arrayAttrItemsList = MongoOpt.find(db, QUERY_ATTR_DEFINE_COLL_NAME, query, sort);
             */
            List<DBObject> arrayAttrItemsList = itemAttrsMap.get(queryAttrDefId);
            if (arrayAttrItemsList == null || arrayAttrItemsList.isEmpty()) {
                continue;
            }

            List<DBObject> itemsOfArrayAttrList = new ArrayList<DBObject>();//缓存去重后的单字段信息

            for (DBObject arrayAttrItemDoc : arrayAttrItemsList) {

                int displayFlag = MongoUtil.getIntValue(arrayAttrItemDoc, "display_flag");
                if (displayFlag == 0) {
                    continue;
                }

                String attrName = MongoUtil.getStringValue(arrayAttrItemDoc, "attr_name"); //字段名
                if (attrName == null || attrName.trim().isEmpty()) {
                    continue;
                }

                String colFieldName = MongoUtil.getStringValue(arrayAttrItemDoc, "col_field_name"); //目标集合字段名
                if (colFieldName == null || colFieldName.trim().isEmpty()) {
                    continue;
                }

                fieldsSet.add(colFieldName);

                DBObject itemAttrDoc = new BasicDBObject();
                itemAttrDoc.put("attr_name", attrName);
                itemAttrDoc.put("attr_display_name", MongoUtil.getStringValue(arrayAttrItemDoc, "attr_display_name"));
                itemAttrDoc.put("display_flag", MongoUtil.getIntValue(arrayAttrItemDoc, "display_flag"));
                itemAttrDoc.put("field_spec", MongoUtil.getIntValue(arrayAttrItemDoc, "field_spec"));


                if (!itemsOfArrayAttrList.contains(itemAttrDoc)) {
                    itemsOfArrayAttrList.add(itemAttrDoc);
                }

            }


            queryCollFieldsCacheMap.put(colName, fieldsSet);

            //对每个集合的字段进行处理+++++++++++++++++++++++++++
            ArrayNode arrayAttrItemFieldsNode = JsonNodeFactory.instance.arrayNode();

            //生成集合的字段列表信息
            for (DBObject itemAttrDoc : itemsOfArrayAttrList) {
                ObjectNode itemFieldNode = JsonNodeFactory.instance.objectNode();

                itemFieldNode.put("fieldName", MongoUtil.getStringValue(itemAttrDoc, "attr_name"));
                itemFieldNode.put("fieldDisplayName", MongoUtil.getStringValue(itemAttrDoc, "attr_display_name"));
                itemFieldNode.put("displayFlag", MongoUtil.getIntValue(itemAttrDoc, "display_flag"));
                itemFieldNode.put("fieldSpec", MongoUtil.getIntValue(itemAttrDoc, "field_spec"));

                arrayAttrItemFieldsNode.add(itemFieldNode);
            }


            ObjectNode attrArrayNode = JsonNodeFactory.instance.objectNode();
            attrArrayNode.put("attrCode", MongoUtil.getStringValue(arrayAttrDoc, "attr_name"));
            attrArrayNode.put("attrName", MongoUtil.getStringValue(arrayAttrDoc, "attr_display_name"));
            attrArrayNode.put("itemType", "array");
            attrArrayNode.put("itemFields", arrayAttrItemFieldsNode);

            rootNode.add(attrArrayNode);


        }



        //处理 groupAttr-------------------------------------------

        for (DBObject groupAttrDoc : groupAttrList) {

            long groupAttrDefId = MongoUtil.getLongValue(groupAttrDoc, "query_attr_def_id"); //属性的编号

            /*
             query = new BasicDBObject();
             query.put("query_attrset_id", attrSetId);
             query.put("query_attr_owner_id", groupAttrDefId);
             query.put("query_attr_item_type", 2);
             query.put("display_flag", 1);

             List<DBObject> groupItemsList = MongoOpt.find(db, QUERY_ATTR_DEFINE_COLL_NAME, query, sort);
             */
            List<DBObject> groupItemsList = itemAttrsMap.get(groupAttrDefId);
            if (groupItemsList == null || groupItemsList.isEmpty()) {
                continue;
            }


            ArrayNode groupItemsNode = JsonNodeFactory.instance.arrayNode();

            for (DBObject arrayAttrDoc : groupItemsList) {

                long queryAttrDefId = MongoUtil.getLongValue(arrayAttrDoc, "query_attr_def_id"); //属性的编号
                String colName = MongoUtil.getStringValue(arrayAttrDoc, "col_name");


                Set<String> fieldsSet = queryCollFieldsCacheMap.get(colName);
                if (fieldsSet == null) {
                    fieldsSet = new HashSet<String>();
                }

                /*
                 query = new BasicDBObject();
                 query.put("query_attrset_id", attrSetId);
                 query.put("query_attr_owner_id", queryAttrDefId);
                 query.put("query_attr_item_type", 1);

                 List<DBObject> arrayAttrItemsList = MongoOpt.find(db, QUERY_ATTR_DEFINE_COLL_NAME, query, sort);
                 */

                List<DBObject> arrayAttrItemsList = itemAttrsMap.get(queryAttrDefId);
                if (arrayAttrItemsList == null || arrayAttrItemsList.isEmpty()) {
                    continue;
                }

                List<DBObject> itemsOfArrayAttrList = new ArrayList<DBObject>();//缓存去重后的单字段信息

                for (DBObject arrayAttrItemDoc : arrayAttrItemsList) {

                    int displayFlag = MongoUtil.getIntValue(arrayAttrItemDoc, "display_flag");
                    if (displayFlag == 0) {
                        continue;
                    }

                    String attrName = MongoUtil.getStringValue(arrayAttrItemDoc, "attr_name"); //字段名
                    if (attrName == null || attrName.trim().isEmpty()) {
                        continue;
                    }

                    String colFieldName = MongoUtil.getStringValue(arrayAttrItemDoc, "col_field_name"); //目标集合字段名
                    if (colFieldName == null || colFieldName.trim().isEmpty()) {
                        continue;
                    }

                    fieldsSet.add(colFieldName);

                    DBObject itemAttrDoc = new BasicDBObject();
                    itemAttrDoc.put("attr_name", attrName);
                    itemAttrDoc.put("attr_display_name", MongoUtil.getStringValue(arrayAttrItemDoc, "attr_display_name"));
                    itemAttrDoc.put("display_flag", MongoUtil.getIntValue(arrayAttrItemDoc, "display_flag"));
                    itemAttrDoc.put("field_spec", MongoUtil.getIntValue(arrayAttrItemDoc, "field_spec"));


                    if (!itemsOfArrayAttrList.contains(itemAttrDoc)) {
                        itemsOfArrayAttrList.add(itemAttrDoc);
                    }

                }


                queryCollFieldsCacheMap.put(colName, fieldsSet);

                //对每个集合的字段进行处理+++++++++++++++++++++++++++
                ArrayNode arrayAttrItemFieldsNode = JsonNodeFactory.instance.arrayNode();

                //生成集合的字段列表信息
                for (DBObject itemAttrDoc : itemsOfArrayAttrList) {
                    ObjectNode itemFieldNode = JsonNodeFactory.instance.objectNode();

                    itemFieldNode.put("fieldName", MongoUtil.getStringValue(itemAttrDoc, "attr_name"));
                    itemFieldNode.put("fieldDisplayName", MongoUtil.getStringValue(itemAttrDoc, "attr_display_name"));
                    itemFieldNode.put("displayFlag", MongoUtil.getIntValue(itemAttrDoc, "display_flag"));
                    itemFieldNode.put("fieldSpec", MongoUtil.getIntValue(itemAttrDoc, "field_spec"));

                    arrayAttrItemFieldsNode.add(itemFieldNode);
                }



                ObjectNode attrArrayNode = JsonNodeFactory.instance.objectNode();
                attrArrayNode.put("attrCode", MongoUtil.getStringValue(arrayAttrDoc, "attr_name"));
                attrArrayNode.put("attrName", MongoUtil.getStringValue(arrayAttrDoc, "attr_display_name"));
                attrArrayNode.put("itemType", "array");
                attrArrayNode.put("itemFields", arrayAttrItemFieldsNode);

                groupItemsNode.add(attrArrayNode);
            }


            ObjectNode groupAttrNode = JsonNodeFactory.instance.objectNode();
            groupAttrNode.put("attrCode", MongoUtil.getStringValue(groupAttrDoc, "attr_name"));
            groupAttrNode.put("attrName", MongoUtil.getStringValue(groupAttrDoc, "attr_display_name"));
            groupAttrNode.put("itemType", "group");
            groupAttrNode.put("groupItems", groupItemsNode);

            rootNode.add(groupAttrNode);

        }


        return rootNode;
    }

    //获取值列表*************************
    public static JsonNode getRows(AuthUserContext modelContext,DB dbConfig, DB dbData, String dimensionParam, Map<String, String> paramMap) {
        ObjectNode rootNode = JsonNodeFactory.instance.objectNode();


        JsonNode attrSetArrayNode = getRowsOfAttrSetByDimension(modelContext,dbConfig, dbData, dimensionParam, paramMap);

        rootNode.put("dimensionCode", dimensionParam);
        rootNode.put("attrSet", attrSetArrayNode);

        System.out.println("查询数据库共花费时长-->" + queryCollTotalSpendTime + "S");

        queryCollTotalSpendTime = 0;

        queryCollFieldsCacheMap.clear();
        queryCollResultCacheMap.clear();

        return rootNode;
    }

    private static JsonNode getRowsOfAttrSetByDimension(AuthUserContext modelContext,DB dbConfig, DB dbData, String dimensionParam, Map<String, String> paramMap) {
        ArrayNode rootNode = JsonNodeFactory.instance.arrayNode();
        //Object localObject = null;

        Map<Long, Map<String, String>> newParamMap = parseParamMap(dbConfig, dimensionParam, paramMap);
        Map<Long, String> atrrSetCollMap = getAtrrSetCollMap(dbConfig);

        //resultOfCollMap = new HashMap<String, List<DBObject>>(); //将从每个集合中查到的记录缓存，以提高查询效率


        //查询非聚合属性集的字段值    

        DBObject query = new BasicDBObject();
        query.put("query_dimension_name", dimensionParam);
        query.put("aggr_flag", 0);
        query.put("enable_flag", 1);

        DBObject sort = new BasicDBObject();
        sort.put("display_order", 1);

        DBObject fieldDoc = new BasicDBObject();
        fieldDoc.put("query_attrset_id", 1);
        fieldDoc.put("query_attrset_name", 1);
        fieldDoc.put("display_order", 1);

        List<DBObject> attrSetList = MongoOpt.find(dbConfig, QUERY_DIMENSION_ATTRSET_COLL_NAME, query, fieldDoc, sort);


        for (DBObject attrSetDoc : attrSetList) {
            long queryAttrSetId = MongoUtil.getLongValue(attrSetDoc, "query_attrset_id");//查询属性集编号

            String queryAttrSetName = MongoUtil.getStringValue(attrSetDoc, "query_attrset_name");//属性集名称
            if (queryAttrSetName == null || queryAttrSetName.trim().isEmpty()) {
                continue;
            }

            Map<String, String> paramMapOfAttrSet = newParamMap.get(queryAttrSetId);
            if (paramMapOfAttrSet == null) {
                continue;
            }

            System.out.println("\n属性集编号-->" + queryAttrSetId);

            JsonNode attrNode = getRowsOfAttrByAttrSet(modelContext,dbConfig, dbData, queryAttrSetId, paramMapOfAttrSet, atrrSetCollMap); //查询当前属性集的下属属性

            /*
             if (queryAttrSetId == 1 && attrNode == null) {
             return rootNode;
             }
             */


            //判断attr节点是否为空
            //if (attrNode != null) {
            ObjectNode attrSetNode = JsonNodeFactory.instance.objectNode();
            attrSetNode.put("attrSetCode", queryAttrSetName);
            // attrSetNode.put("attrSetName", MongoUtil.getStringValue(attrSetDoc, "query_attrset_display_name"));
            attrSetNode.put("attr", attrNode);

            rootNode.add(attrSetNode);
            // }

        }

        //查询聚合属性集的字段值 

        query = new BasicDBObject();
        query.put("query_dimension_name", dimensionParam);
        query.put("aggr_flag", 1);
        query.put("enable_flag", 1);

        fieldDoc = new BasicDBObject();
        fieldDoc.put("query_attrset_name", 1);
        fieldDoc.put("display_order", 1);

        List<DBObject> aggrAttrSetList = MongoOpt.find(dbConfig, QUERY_DIMENSION_ATTRSET_COLL_NAME, query, fieldDoc, sort);


        //对聚合属性集进行二次查询
        for (DBObject aggrAttrSetDoc : aggrAttrSetList) {
            String aggrAttrSetName = MongoUtil.getStringValue(aggrAttrSetDoc, "query_attrset_name"); //属性集合名称
            if (aggrAttrSetName == null || aggrAttrSetName.trim().isEmpty()) {
                continue;
            }


            query = new BasicDBObject();
            query.put("query_dimension_name", aggrAttrSetName);
            query.put("aggr_flag", 0);
            query.put("enable_flag", 1);

            fieldDoc = new BasicDBObject();
            fieldDoc.put("query_attrset_id", 1);
            fieldDoc.put("query_attrset_name", 1);
            fieldDoc.put("display_order", 1);

            List<DBObject> aggrItemAttrSetList = MongoOpt.find(dbConfig, QUERY_DIMENSION_ATTRSET_COLL_NAME, query, fieldDoc, sort);


            ArrayNode aggrAttrNode = JsonNodeFactory.instance.arrayNode();

            //查询非聚合属性集的字段信息
            for (DBObject attrSetDoc : aggrItemAttrSetList) {
                long queryAttrSetId = MongoUtil.getLongValue(attrSetDoc, "query_attrset_id");//查询属性集编号

                String queryAttrSetName = MongoUtil.getStringValue(attrSetDoc, "query_attrset_name"); //属性集合名称
                if (queryAttrSetName == null || queryAttrSetName.trim().isEmpty()) {
                    continue;
                }

                Map<String, String> paramMapOfAttrSet = newParamMap.get(queryAttrSetId);

                JsonNode attrNode = getRowsOfAttrByAttrSet(modelContext,dbConfig, dbData, queryAttrSetId, paramMapOfAttrSet, atrrSetCollMap); //查询当前属性集的下属属性

                //if (attrNode.size() > 0) {
                ObjectNode attrSetNode = JsonNodeFactory.instance.objectNode();
                attrSetNode.put("attrSetCode", queryAttrSetName);
                attrSetNode.put("attr", attrNode);

                aggrAttrNode.add(attrSetNode);
                // }
            }

            if (aggrAttrNode.size() > 0) {
                ObjectNode AggeAttrSetNode = JsonNodeFactory.instance.objectNode();
                AggeAttrSetNode.put("attrSetCode", aggrAttrSetName);
                AggeAttrSetNode.put("attr", aggrAttrNode);
                //-------------------------------------------
                rootNode.add(AggeAttrSetNode);
            }
        }
        //
        return rootNode;
    }

    private static JsonNode getRowsOfAttrByAttrSet(AuthUserContext modelContext,DB dbConfig, DB dbData, long attrSetId, Map<String, String> paramMapOfAttrSet, Map<Long, String> atrrSetCollMap) {
        ArrayNode rootNode = JsonNodeFactory.instance.arrayNode();

        //String collName = QUERY_ATTR_DEFINE_COLL_NAME;

        DBObject query = new BasicDBObject();
        query.put("query_attrset_id", attrSetId);
        //query.put("query_attr_owner_id", 0);

        DBObject sort = new BasicDBObject();
        sort.put("field_order", 1);

        DBObject fieldDoc = new BasicDBObject();
        fieldDoc.put("query_attrset_id", 0);

        List<DBObject> attrsOfAttrSetList = MongoOpt.find(dbConfig, QUERY_ATTR_DEFINE_COLL_NAME, query, fieldDoc, sort);


        Map<String, String> queryParamMap = null;
        List<DBObject> arrayAttrList = new ArrayList<DBObject>();
        List<DBObject> groupAttrList = new ArrayList<DBObject>();
        Map<Long, List<DBObject>> itemAttrsMap = new HashMap<Long, List<DBObject>>();

        List<String> colNameList = new ArrayList<String>();
        Map<String, List<DBObject>> itemsOfColNameMap = new HashMap<String, List<DBObject>>();

        List<String> attrList = new ArrayList<String>();
        Map<String, Map<String, String>> attrMap = new HashMap<String, Map<String, String>>();

        //Map<String, List<DBObject>> resultOfCollMap = new HashMap<String, List<DBObject>>(); //将从每个集合中查到的记录缓存，以提高查询效率

        String baseAttrColName = null;

        Object localObject = null;
        for (DBObject attrDoc : attrsOfAttrSetList) {

            long queryAttrOwnerId = MongoUtil.getIntValue(attrDoc, "query_attr_owner_id");
            if (queryAttrOwnerId != 0) {
                List<DBObject> itemAttrsList = itemAttrsMap.get(queryAttrOwnerId);

                if (itemAttrsList == null) {
                    itemAttrsList = new ArrayList<DBObject>();
                }
                itemAttrsList.add(attrDoc);
                itemAttrsMap.put(queryAttrOwnerId, itemAttrsList);

                continue;
            }

            String attrName = MongoUtil.getStringValue(attrDoc, "attr_name"); //字段名
            if (attrName == null || attrName.trim().isEmpty()) {
                continue;
            }


            Integer queryAttrItemType = MongoUtil.getIntValue(attrDoc, "query_attr_item_type");//属性的数据类别：1 单个元素 item, 2 多个元素 array     
            int displayFlag = MongoUtil.getIntValue(attrDoc, "display_flag"); //显示标志


            if (queryAttrItemType == 1) {

                String colName = MongoUtil.getStringValue(attrDoc, "col_name"); //目标集合名 
                if (colName == null || colName.trim().isEmpty()) {
                    continue;
                }

                String colFieldName = MongoUtil.getStringValue(attrDoc, "col_field_name"); //目标集合字段名
                if (colFieldName == null || colFieldName.trim().isEmpty()) {
                    continue;
                }

                List<DBObject> itemsOfAttrNameList = itemsOfColNameMap.get(colName);
                if (itemsOfAttrNameList == null) {
                    itemsOfAttrNameList = new ArrayList<DBObject>();
                    itemsOfColNameMap.put(colName, itemsOfAttrNameList);
                }
                itemsOfAttrNameList.add(attrDoc);

                if (!colNameList.contains(colName)) {
                    colNameList.add(colName);
                }

                //收集单字段名称
                attrMap.put(attrName, new HashMap<String, String>());

                if (displayFlag != 0 && !attrList.contains(attrName)) {
                    attrList.add(attrName);
                }

                /*
                 int paramFlag = MongoUtil.getIntValue(attrDoc, "param_flag"); //是否用来做匹配参数
                 if (paramFlag == 1 && displayFlag == 1) {
                 baseAttrColName = colName;
                 }
                 */

            } else {

                if (displayFlag == 0) {
                    continue;
                }

                if (queryAttrItemType == 2) {

                    String colName = MongoUtil.getStringValue(attrDoc, "col_name"); //目标集合名 
                    if (colName == null || colName.trim().isEmpty()) {
                        continue;
                    }
                    arrayAttrList.add(attrDoc);

                } else if (queryAttrItemType == 3) {

                    groupAttrList.add(attrDoc);

                }

            }

        }

        //处理属性集下属的 itemAttr-----------------------------------------------------------------------

        handleItemAttr:
        {

            boolean isPersonInLocalBaseColl = false;

            for (String colNameKey : colNameList) {

                queryParamMap = new HashMap<String, String>();
                List<DBObject> valueDocList = null;
                sort = new BasicDBObject();

                List<String> attrOfCollList = new ArrayList<String>();
                Map<String, List<DBObject>> itemsOfAttrNameMap = new HashMap<String, List<DBObject>>();

                Set<String> fieldsOfCollSet = new HashSet<String>();

                List<DBObject> itemsOfColNameList = itemsOfColNameMap.get(colNameKey);
                for (DBObject attrDoc : itemsOfColNameList) {

                    String attrName = MongoUtil.getStringValue(attrDoc, "attr_name"); //字段名
                    String colFieldName = MongoUtil.getStringValue(attrDoc, "col_field_name"); //目标集合字段名
                    int paramFlag = MongoUtil.getIntValue(attrDoc, "param_flag"); //是否用来做匹配参数
                    int orderFlag = MongoUtil.getIntValue(attrDoc, "order_flag");  //排序标志

                    fieldsOfCollSet.add(colFieldName);

                    if (orderFlag == 1) {
                        sort.put(colFieldName, 1);
                    } else if (orderFlag == 2) {
                        sort.put(colFieldName, -1);
                    }
                    //将查询参数中的字段名转换成数据集合中的真实字段名
                    if (paramFlag == 1) {
                        if (paramMapOfAttrSet.containsKey(attrName)) {
                            String paramValue = paramMapOfAttrSet.get(attrName);
                            queryParamMap.put(colFieldName, paramValue);
                        }
                    }

                    int displayFlag = MongoUtil.getIntValue(attrDoc, "display_flag");
                    if (displayFlag == 0) {
                        continue;
                    }

                    List<DBObject> itemsOfAttrNameList = itemsOfAttrNameMap.get(attrName);
                    if (itemsOfAttrNameList == null) {
                        itemsOfAttrNameList = new ArrayList<DBObject>();
                        itemsOfAttrNameMap.put(attrName, itemsOfAttrNameList);
                    }
                    itemsOfAttrNameList.add(attrDoc);

                    if (!attrOfCollList.contains(attrName)) {
                        attrOfCollList.add(attrName);
                    }
                }


                if (colNameKey == null || colNameKey.trim().isEmpty()) {
                    colNameKey = atrrSetCollMap.get(attrSetId);
                }

                if (colNameKey == null || colNameKey.trim().isEmpty()) {
                    continue;
                }

                if (queryParamMap.size() != paramMapOfAttrSet.size()) {
                    continue;
                }


                query = parseParamMapToQueryDBObject(queryParamMap);

                Map<String, Object> extraQueryParamOfCollMap = extraQueryParamOfAllCollMap.get(colNameKey);
                System.out.println(colNameKey+"-->"+extraQueryParamOfCollMap);
                
                if (extraQueryParamOfCollMap != null) {
                    for (String key : extraQueryParamOfCollMap.keySet()) {
                    query.put(key, extraQueryParamOfCollMap.get(key));
                    }
                }


                System.out.println("\n查询集合名称-->" + colNameKey);
                System.out.println("查询条件-->" + query);

                fieldDoc = new BasicDBObject();
                for (String colField : fieldsOfCollSet) {
                    fieldDoc.put(colField, 1);
                }

                Date startTime = new Date();

                valueDocList = MongoOpt.find(dbData, colNameKey, query, fieldDoc, sort, 1);//对每个集合只取一条记录

                Date endTime = new Date();
                double spendTime = (double) (endTime.getTime() - startTime.getTime()) / 1000;
                queryCollTotalSpendTime += spendTime;
                System.out.println("花费时长-->" + spendTime + "S");



                if (valueDocList.isEmpty()) {
                    baseAttrColName = colNameKey;
                    continue;
                }
                //if (attrSetId == 1 && colNameKey.equals(baseAttrColName)) {
                isPersonInLocalBaseColl = true;
                //}


                //处理 colNameKey 下属的 itemAttr-----------------------------------------------------

                for (String attrKey : attrOfCollList) {
                    List<DBObject> itemsOfAttrNameList = itemsOfAttrNameMap.get(attrKey);

                    List<String> itemValuesList = new ArrayList<String>();
                    for (DBObject valueDoc : valueDocList) {

                        for (DBObject attrDoc : itemsOfAttrNameList) {

                            String colFieldName = MongoUtil.getStringValue(attrDoc, "col_field_name"); //目标集合字段名

                            localObject = valueDoc.get(colFieldName);
                            if (localObject == null) {
                                continue;
                            }

                            String value = parseValueToString(localObject);
                            if (value == null || value.trim().isEmpty()) {
                                continue;
                            }

                            itemValuesList.add(value);
                        }

                    }


                    if (itemValuesList.isEmpty()) {
                        continue;
                    }

                    Map<String, String> attrValueMap = attrMap.get(attrKey);
                    if (attrValueMap == null) {
                        continue;
                    }


                    String attrValueOfCol = "";
                    int index = 1;
                    for (String value : itemValuesList) {

                        if (index == 1) {
                            attrValueOfCol += value;
                        } else {
                            attrValueOfCol += "," + value;
                        }

                        index++;

                    }

                    attrValueMap.put(colNameKey, attrValueOfCol);
                }

            }


            if (attrSetId == 1 && isPersonInLocalBaseColl != true) {

                //在mongo数据库中的全国人口备份库中进行查找
                System.out.println("去mongo数据库中的全国人口信息备份库中查找-->" + paramMapOfAttrSet);
                boolean isExistInMongo = QueryGABQGRKDao.queryPersonBaseInfoFromQGRKBackup(dbData, paramMapOfAttrSet, attrMap, baseAttrColName);

                if (isExistInMongo != true) {
                    //去全国人口信息库中查找
                    System.out.println("去全国人口信息库中查找-->" + paramMapOfAttrSet);
                    attrMap = QueryGABQGRKDao.queryPersonInfo(modelContext,dbConfig, paramMapOfAttrSet, attrMap, baseAttrColName);
                }

            }

            if (attrMap != null) {

                for (String attrName : attrList) {

                    List<String> attrValueList = new ArrayList<String>();
                    String attrValue = "";

                    if (paramMapOfAttrSet.containsKey(attrName)) {

                        attrValue = paramMapOfAttrSet.get(attrName);
                        String[] attrValueArray = attrValue.split(",");

                        for (String tempAttrValue : attrValueArray) {
                            if (!attrValueList.contains(tempAttrValue)) {
                                attrValueList.add(tempAttrValue);
                            }
                        }

                    } else {

                        Map<String, String> attrValueMap = attrMap.get(attrName);
                        Set<String> KeySet = attrValueMap.keySet();

                        if (KeySet.size() == 1) {
                            Iterator<String> it = KeySet.iterator();
                            if (it.hasNext()) {
                                String key = it.next();
                                attrValue = attrValueMap.get(key);
                                String[] attrValueArray = attrValue.split(",");

                                for (String tempAttrValue : attrValueArray) {
                                    if (!attrValueList.contains(tempAttrValue)) {
                                        attrValueList.add(tempAttrValue);
                                    }
                                }

                            }

                        } else if (KeySet.size() > 1) {

                            for (String colNameKey : KeySet) {

                                attrValue = attrValueMap.get(colNameKey);
                                String[] attrValueArray = attrValue.split(",");

                                for (String tempAttrValue : attrValueArray) {
                                    if (!attrValueList.contains(tempAttrValue)) {
                                        attrValueList.add(tempAttrValue);
                                    }
                                }

                            }

                        }

                    }


                    ObjectNode attrNode = JsonNodeFactory.instance.objectNode();

                    attrValue = "";
                    for (int i = 0; i < attrValueList.size(); i++) {
                        String tempAttrValue = attrValueList.get(i);

                        if (i > 0) {
                            tempAttrValue = "," + tempAttrValue;
                        }

                        attrValue += tempAttrValue;
                    }


                    attrNode.put("attrCode", attrName);
                    //attrNode.put("attrName", attrDisplayNameMap.get(attrName));
                    attrNode.put("itemType", "item");
                    attrNode.put("attrValue", attrValue);

                    rootNode.add(attrNode);
                }

            }

        }//end handleItemAttr




        //处理属性集中的 arrayAttr-----------------------------------------------------------------------

        handleArrayAttr:
        {

            for (DBObject arrayAttrDoc : arrayAttrList) {

                long queryAttrDefId = MongoUtil.getLongValue(arrayAttrDoc, "query_attr_def_id"); //属性的编号
                String arrayAttrColName = MongoUtil.getStringValue(arrayAttrDoc, "col_name"); //目标集合名

                /*
                 query = new BasicDBObject();
                 query.put("query_attrset_id", attrSetId);
                 query.put("query_attr_owner_id", queryAttrDefId);
                 query.put("query_attr_item_type", 1);

                 sort = new BasicDBObject();
                 sort.put("field_order", 1);

                 List<DBObject> attrItemList = MongoOpt.find(dbConfig, QUERY_ATTR_DEFINE_COLL_NAME, query, sort);
                 */

                List<DBObject> attrItemList = itemAttrsMap.get(queryAttrDefId);
                if (attrItemList == null || attrItemList.isEmpty()) {
                    continue;
                }

                //处理下属的的 itemAttr-----------------------------------

                queryParamMap = new HashMap<String, String>();
                List<DBObject> valueDocList = null;
                sort = new BasicDBObject();

                List<String> attrNameList = new ArrayList<String>();
                Map<String, List<DBObject>> itemsOfAttrNameMap = new HashMap<String, List<DBObject>>();

                List<String> distinctFieldList = new ArrayList<String>();//去重字段列表

                //Set<String> fieldsOfCollSet = new HashSet<String>();

                for (DBObject attrDoc : attrItemList) {

                    String attrName = MongoUtil.getStringValue(attrDoc, "attr_name"); //字段名

                    String colFieldName = MongoUtil.getStringValue(attrDoc, "col_field_name"); //目标集合字段名
                    if (colFieldName == null || colFieldName.trim().isEmpty()) {
                        continue;
                    }
                    //fieldsOfCollSet.add(colFieldName);

                    Integer paramFlag = MongoUtil.getIntValue(attrDoc, "param_flag"); //是否用来做匹配参数
                    Integer orderFlag = MongoUtil.getIntValue(attrDoc, "order_flag");  //排序标志
                    if (orderFlag == 1) {
                        sort.put(colFieldName, 1);
                    } else if (orderFlag == 2) {
                        sort.put(colFieldName, -1);
                    }
                    //将查询参数中的字段名转换成数据集合中的真实字段名
                    if (paramFlag == 1) {
                        if (paramMapOfAttrSet.containsKey(attrName)) {
                            String paramValue = paramMapOfAttrSet.get(attrName);
                            queryParamMap.put(colFieldName, paramValue);
                        }
                    }

                    int displayFlag = MongoUtil.getIntValue(attrDoc, "display_flag");
                    if (displayFlag == 0) {
                        continue;
                    }

                    int distinctFlag = MongoUtil.getIntValue(attrDoc, "distinct_flag"); //去重标志
                    if (distinctFlag == 1) {
                        distinctFieldList.add(attrName);
                    }


                    List<DBObject> itemsOfAttrNameList = itemsOfAttrNameMap.get(attrName);
                    if (itemsOfAttrNameList == null) {
                        itemsOfAttrNameList = new ArrayList<DBObject>();
                        itemsOfAttrNameMap.put(attrName, itemsOfAttrNameList);
                    }
                    itemsOfAttrNameList.add(attrDoc);

                    if (!attrNameList.contains(attrName)) {
                        attrNameList.add(attrName);
                    }
                }

                if (queryParamMap.size() != paramMapOfAttrSet.size()) {
                    continue;
                }

                valueDocList = queryCollResultCacheMap.get(arrayAttrColName);
                if (valueDocList == null) {

                    query = parseParamMapToQueryDBObject(queryParamMap);

                    //添加扩展的查询条件--------------------------

                    /*
                     Map<String, Object> extendParamMap = getExtendParamOfArrayAttr(dbConfig, queryAttrDefId);
                     Set<String> extendParamKeySet = extendParamMap.keySet();

                     for (String extendParamName : extendParamKeySet) {
                     query.put(extendParamName, extendParamMap.get(extendParamName));
                     }
                     */

                    //------------------------------------------

                    System.out.println("\n查询集合名称-->" + arrayAttrColName);
                    System.out.println("查询条件-->" + query);

                    fieldDoc = new BasicDBObject();
                    /*
                     for (String colField : fieldsOfCollSet) {
                     fieldDoc.put(colField, 1);
                     }
                     */


                    Set<String> fieldsOfCollSet = queryCollFieldsCacheMap.get(arrayAttrColName);
                    if (fieldsOfCollSet != null) {

                        for (String colField : fieldsOfCollSet) {
                            fieldDoc.put(colField, 1);
                        }

                    }


                    Date startTime = new Date();

                    valueDocList = MongoOpt.find(dbData, arrayAttrColName, query, fieldDoc, sort, getMaxRows());

                    queryCollResultCacheMap.put(arrayAttrColName, valueDocList);

                    Date endTime = new Date();
                    double spendTime = (double) (endTime.getTime() - startTime.getTime()) / 1000;
                    queryCollTotalSpendTime += spendTime;
                    System.out.println("花费时长-->" + spendTime + "S");
                }

                if (valueDocList.isEmpty()) {
                    continue;
                }



                List<JsonNode> itemFieldsList = new ArrayList<JsonNode>();
                List<String> distinctValueList = new ArrayList<String>();//去重值列表

                //对每一个attr进行处理-----------------------------------------
                for (DBObject valueDoc : valueDocList) {

                    String distinctValue = "";
                    boolean isNotEmptyFlag = false;


                    ObjectNode itemNode = JsonNodeFactory.instance.objectNode();
                    for (String attrKey : attrNameList) {
                        List<String> itemValuesList = new ArrayList<String>();

                        List<DBObject> itemsOfAttrNameList = itemsOfAttrNameMap.get(attrKey);
                        for (DBObject attrDoc : itemsOfAttrNameList) {
                            //对显示标志进行判断
                            int displayFlag = MongoUtil.getIntValue(attrDoc, "display_flag");
                            if (displayFlag == 0) {
                                continue;
                            }

                            String colFieldName = MongoUtil.getStringValue(attrDoc, "col_field_name"); //目标集合字段名
                            localObject = valueDoc.get(colFieldName);
                            if (localObject == null) {
                                continue;
                            }
                            String value = parseValueToString(localObject);
                            if (value == null || value.trim().isEmpty()) {
                                continue;
                            }

                            if (!itemValuesList.contains(value)) {
                                itemValuesList.add(value);
                            }
                        }

                        String finalValue = "";
                        int index = 1;
                        for (String value : itemValuesList) {
                            if (index == 1) {
                                finalValue += value;
                            } else {
                                finalValue += "," + value;
                            }
                            index++;
                        }
                        if (!finalValue.trim().isEmpty()) {
                            isNotEmptyFlag = true;
                        }

                        //添加单条记录中的单个字段
                        itemNode.put(attrKey, finalValue);

                        if (distinctFieldList.contains(attrKey)) {
                            distinctValue += finalValue;
                        }
                    }

                    //对本条记录进行去重操作
                    if ((!distinctFieldList.isEmpty()) && distinctValueList.contains(distinctValue)) {
                        continue;
                    }
                    distinctValueList.add(distinctValue);

                    //添加单条记录
                    if (isNotEmptyFlag != false && !itemFieldsList.contains(itemNode)) {
                        itemFieldsList.add(itemNode);
                    }
                }

                if (itemFieldsList.isEmpty()) {
                    continue;
                }

                ArrayNode itemFieldsNode = JsonNodeFactory.instance.arrayNode();
                for (JsonNode itemNode : itemFieldsList) {
                    itemFieldsNode.add(itemNode);
                }

                ObjectNode attrArrayNode = JsonNodeFactory.instance.objectNode();

                attrArrayNode.put("attrCode", MongoUtil.getStringValue(arrayAttrDoc, "attr_name"));//字段名
                attrArrayNode.put("attrName", MongoUtil.getStringValue(arrayAttrDoc, "attr_display_name"));
                attrArrayNode.put("itemType", "array");
                attrArrayNode.put("itemFields", itemFieldsNode);

                rootNode.add(attrArrayNode);

            }

        }//end handleArrayAttr




        //处理属性集下属的 groupAttr------------------------------------------------------

        for (DBObject groupAttrDoc : groupAttrList) {

            long groupAttrDefId = MongoUtil.getLongValue(groupAttrDoc, "query_attr_def_id");

            /*
             query = new BasicDBObject();
             query.put("query_attrset_id", attrSetId);
             query.put("query_attr_owner_id", groupAttrDefId);
             query.put("query_attr_item_type", 2);
             query.put("display_flag", 1);

             sort = new BasicDBObject();
             sort.put("field_order", 1);

             List<DBObject> groupItemList = MongoOpt.find(dbConfig, QUERY_ATTR_DEFINE_COLL_NAME, query, sort);
             */

            List<DBObject> groupItemList = itemAttrsMap.get(groupAttrDefId);
            if (groupItemList == null || groupItemList.isEmpty()) {
                continue;
            }

            ArrayNode groupItemsNode = JsonNodeFactory.instance.arrayNode();


            //处理 group 下属的 arrayAttr---------------------------
            handleGroupItems:
            {

                for (DBObject arrayAttrDoc : groupItemList) {

                    long queryAttrDefId = MongoUtil.getLongValue(arrayAttrDoc, "query_attr_def_id"); //属性的编号
                    String arrayAttrColName = MongoUtil.getStringValue(arrayAttrDoc, "col_name"); //目标集合名

                    /*
                     query = new BasicDBObject();
                     query.put("query_attrset_id", attrSetId);
                     query.put("query_attr_owner_id", queryAttrDefId);
                     query.put("query_attr_item_type", 1);

                     sort = new BasicDBObject();
                     sort.put("field_order", 1);

                     List<DBObject> attrItemList = MongoOpt.find(dbConfig, QUERY_ATTR_DEFINE_COLL_NAME, query, sort);
                     */

                    List<DBObject> attrItemList = itemAttrsMap.get(queryAttrDefId);
                    if (attrItemList == null || attrItemList.isEmpty()) {
                        continue;
                    }

                    //处理下属的的 itemAttr-----------------------------------

                    queryParamMap = new HashMap<String, String>();
                    List<DBObject> valueDocList = null;
                    sort = new BasicDBObject();

                    List<String> attrNameList = new ArrayList<String>();
                    Map<String, List<DBObject>> itemsOfAttrNameMap = new HashMap<String, List<DBObject>>();

                    List<String> distinctFieldList = new ArrayList<String>();//去重字段列表

                    //Set<String> fieldsOfCollSet = new HashSet<String>();

                    for (DBObject attrDoc : attrItemList) {

                        String attrName = MongoUtil.getStringValue(attrDoc, "attr_name"); //字段名

                        String colFieldName = MongoUtil.getStringValue(attrDoc, "col_field_name"); //目标集合字段名
                        if (colFieldName == null || colFieldName.trim().isEmpty()) {
                            continue;
                        }
                        //fieldsOfCollSet.add(colFieldName);

                        Integer paramFlag = MongoUtil.getIntValue(attrDoc, "param_flag"); //是否用来做匹配参数
                        Integer orderFlag = MongoUtil.getIntValue(attrDoc, "order_flag");  //排序标志
                        if (orderFlag == 1) {
                            sort.put(colFieldName, 1);
                        } else if (orderFlag == 2) {
                            sort.put(colFieldName, -1);
                        }
                        //将查询参数中的字段名转换成数据集合中的真实字段名
                        if (paramFlag == 1) {
                            if (paramMapOfAttrSet.containsKey(attrName)) {
                                String paramValue = paramMapOfAttrSet.get(attrName);
                                queryParamMap.put(colFieldName, paramValue);
                            }
                        }

                        int displayFlag = MongoUtil.getIntValue(attrDoc, "display_flag");
                        if (displayFlag == 0) {
                            continue;
                        }

                        int distinctFlag = MongoUtil.getIntValue(attrDoc, "distinct_flag"); //去重标志
                        if (distinctFlag == 1) {
                            distinctFieldList.add(attrName);
                        }


                        List<DBObject> itemsOfAttrNameList = itemsOfAttrNameMap.get(attrName);
                        if (itemsOfAttrNameList == null) {
                            itemsOfAttrNameList = new ArrayList<DBObject>();
                            itemsOfAttrNameMap.put(attrName, itemsOfAttrNameList);
                        }
                        itemsOfAttrNameList.add(attrDoc);

                        if (!attrNameList.contains(attrName)) {
                            attrNameList.add(attrName);
                        }
                    }


                    if (queryParamMap.size() != paramMapOfAttrSet.size()) {
                        continue;
                    }


                    valueDocList = queryCollResultCacheMap.get(arrayAttrColName);
                    if (valueDocList == null) {

                        query = parseParamMapToQueryDBObject(queryParamMap);

                        //添加扩展的查询条件--------------------------

                        /*
                         Map<String, Object> extendParamMap = getExtendParamOfArrayAttr(dbConfig, queryAttrDefId);
                         Set<String> extendParamKeySet = extendParamMap.keySet();

                         for (String extendParamName : extendParamKeySet) {
                         query.put(extendParamName, extendParamMap.get(extendParamName));
                         }
                         */

                        //------------------------------------------

                        System.out.println("\n查询集合名称-->" + arrayAttrColName);
                        System.out.println("查询条件-->" + query);


                        fieldDoc = new BasicDBObject();

                        /*
                         for (String colField : fieldsOfCollSet) {
                         fieldDoc.put(colField, 1);
                         }
                         */


                        Set<String> fieldsOfCollSet = queryCollFieldsCacheMap.get(arrayAttrColName);
                        if (fieldsOfCollSet != null) {

                            for (String colField : fieldsOfCollSet) {
                                fieldDoc.put(colField, 1);
                            }

                        }


                        Date startTime = new Date();

                        valueDocList = MongoOpt.find(dbData, arrayAttrColName, query, fieldDoc, sort, getMaxRows());

                        queryCollResultCacheMap.put(arrayAttrColName, valueDocList);

                        Date endTime = new Date();
                        double spendTime = (double) (endTime.getTime() - startTime.getTime()) / 1000;
                        queryCollTotalSpendTime += spendTime;
                        System.out.println("花费时长-->" + spendTime + "S");
                    }


                    if (valueDocList.isEmpty()) {
                        continue;
                    }


                    List<JsonNode> itemFieldsList = new ArrayList<JsonNode>();
                    List<String> distinctValueList = new ArrayList<String>();//去重值列表

                    //对每一个attr进行处理-----------------------------------------
                    for (DBObject valueDoc : valueDocList) {

                        String distinctValue = "";
                        boolean isNotEmptyFlag = false;


                        ObjectNode itemNode = JsonNodeFactory.instance.objectNode();
                        for (String attrKey : attrNameList) {
                            List<String> itemValuesList = new ArrayList<String>();

                            List<DBObject> itemsOfAttrNameList = itemsOfAttrNameMap.get(attrKey);
                            for (DBObject attrDoc : itemsOfAttrNameList) {

                                //对显示标志进行判断
                                int displayFlag = MongoUtil.getIntValue(attrDoc, "display_flag");
                                if (displayFlag == 0) {
                                    continue;
                                }

                                String colFieldName = MongoUtil.getStringValue(attrDoc, "col_field_name"); //目标集合字段名
                                localObject = valueDoc.get(colFieldName);
                                if (localObject == null) {
                                    continue;
                                }

                                String value = parseValueToString(localObject);
                                if (value == null || value.trim().isEmpty()) {
                                    continue;
                                }


                                if (!itemValuesList.contains(value)) {
                                    itemValuesList.add(value);
                                }
                            }

                            String finalValue = "";
                            int index = 1;
                            for (String value : itemValuesList) {
                                if (index == 1) {
                                    finalValue += value;
                                } else {
                                    finalValue += "," + value;
                                }
                                index++;
                            }
                            if (!finalValue.trim().isEmpty()) {
                                isNotEmptyFlag = true;
                            }

                            //添加单条记录中的单个字段
                            itemNode.put(attrKey, finalValue);

                            if (distinctFieldList.contains(attrKey)) {
                                distinctValue += finalValue;
                            }
                        }


                        //对本条记录进行去重操作
                        if ((!distinctFieldList.isEmpty()) && distinctValueList.contains(distinctValue)) {
                            continue;
                        }


                        distinctValueList.add(distinctValue);

                        //添加单条记录
                        if (isNotEmptyFlag != false && !itemFieldsList.contains(itemNode)) {
                            itemFieldsList.add(itemNode);
                        }
                    }


                    if (itemFieldsList.isEmpty()) {
                        continue;
                    }


                    ArrayNode itemFieldsNode = JsonNodeFactory.instance.arrayNode();
                    for (JsonNode itemNode : itemFieldsList) {
                        itemFieldsNode.add(itemNode);
                    }


                    ObjectNode attrArrayNode = JsonNodeFactory.instance.objectNode();
                    attrArrayNode.put("attrCode", MongoUtil.getStringValue(arrayAttrDoc, "attr_name"));//字段名
                    attrArrayNode.put("attrName", MongoUtil.getStringValue(arrayAttrDoc, "attr_display_name"));
                    attrArrayNode.put("itemType", "array");
                    attrArrayNode.put("itemFields", itemFieldsNode);

                    groupItemsNode.add(attrArrayNode);

                }


            }//end handleGroupItems


            ObjectNode groupAttrNode = JsonNodeFactory.instance.objectNode();
            groupAttrNode.put("attrCode", MongoUtil.getStringValue(groupAttrDoc, "attr_name"));
            groupAttrNode.put("attrName", MongoUtil.getStringValue(groupAttrDoc, "attr_display_name"));
            groupAttrNode.put("itemType", "group");
            groupAttrNode.put("groupItems", groupItemsNode);

            rootNode.add(groupAttrNode);
        }

        return rootNode;
    }

// 原子级接口----------------------------------------------------------------
    private static Map<Long, String> getAtrrSetCollMap(DB db) {
        Map<Long, String> atrrSetCollMap = new HashMap<Long, String>();

        DBCollection coll = db.getCollection(QUERY_ATTRSET_DEFINE_COLL_NAME);
        DBCursor cursor = coll.find();

        cursor.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
        while (cursor.hasNext()) {
            DBObject doc = cursor.next();
            if (doc == null) {
                continue;
            }

            long attrSetId = MongoUtil.getLongValue(doc, "query_attrset_id");

            String attrSetCollName = MongoUtil.getStringValue(doc, "query_attrset_col_name");
            if (attrSetCollName == null || attrSetCollName.trim().isEmpty()) {
                continue;
            }

            atrrSetCollMap.put(attrSetId, attrSetCollName);
        }
        cursor.close();

        return atrrSetCollMap;
    }

    private static Map<Long, Map<String, String>> parseParamMap(DB db, String dimensionParam, Map<String, String> paramMap) {
        Map<Long, Map<String, String>> newParamMap = new HashMap<Long, Map<String, String>>();

        DBCollection coll = db.getCollection(QUERY_ATTRSET_PARAM_COLL_NAME);

        DBObject query = new BasicDBObject();
        query.put("query_dimension_name", dimensionParam);

        DBObject sort = new BasicDBObject();
        sort.put("display_order", 1);

        DBCursor cursor = coll.find(query).sort(sort);

        List<DBObject> list = new ArrayList<DBObject>();
        cursor.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
        while (cursor.hasNext()) {
            DBObject doc = cursor.next();
            if (doc == null) {
                continue;
            }
            list.add(doc);
        }
        cursor.close();

        for (DBObject doc : list) {
            String queryDimensionParam = MongoUtil.getStringValue(doc, "query_dimension_param");

            Set<String> keySet = paramMap.keySet();
            for (String key : keySet) {
                if (!queryDimensionParam.equals(key)) {
                    continue;
                }

                String queryAttrSetField = MongoUtil.getStringValue(doc, "query_attrset_field");
                if (queryAttrSetField == null || queryAttrSetField.trim().isEmpty()) {
                    continue;
                }

                long attrSetId = MongoUtil.getLongValue(doc, "query_attrset_id");

                Map<String, String> paramMapOfAttrSet = newParamMap.get(attrSetId);
                if (paramMapOfAttrSet == null) {
                    paramMapOfAttrSet = new HashMap<String, String>();
                    newParamMap.put(attrSetId, paramMapOfAttrSet);
                }
                paramMapOfAttrSet.put(queryAttrSetField, paramMap.get(key));
            }
        }

        int Nparam = paramMap.size();
        Set<Long> keySet = newParamMap.keySet();
        for (long attrSetId : keySet) {
            Map<String, String> paramMapOfAttrSet = newParamMap.get(attrSetId);
            if (paramMapOfAttrSet.size() != Nparam) {
                newParamMap.remove(attrSetId);
            }
        }

        return newParamMap;
    }

    private static DBObject parseParamMapToQueryDBObject(Map<String, String> paramMap) {
        DBObject queryDoc = new BasicDBObject();

        Set<String> keySet = paramMap.keySet();
        for (String key : keySet) {
            queryDoc.put(key, paramMap.get(key));
        }

        return queryDoc;
    }

    /*
     private static Map getExtendParamOfArrayAttr(DB db, long attrDefId) {
     Map<String, Object> extendParamMap = new HashMap<String, Object>();


     DBCollection coll = db.getCollection(QUERY_ATTR_EXTEND_PARAM_COLL_NAME);

     DBObject query = new BasicDBObject();
     query.put("query_attr_def_id", attrDefId);
     query.put("enable", 1);

     DBCursor cursor = coll.find(query);
     cursor.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
     while (cursor.hasNext()) {
     DBObject doc = cursor.next();

     String paramName = MongoUtil.getStringValue(doc, "param_name");
     if (paramName == null || paramName.isEmpty()) {
     continue;
     }
     Object paramValue = doc.get("param_value");

     extendParamMap.put(paramName, paramValue);
     }
     cursor.close();

     return extendParamMap;
     }
     */
    private static String parseValueToString(Object object) {
        String localStr = "";
        if (object == null) {
            return localStr;
        } else if (object instanceof Boolean) {
            localStr = String.valueOf((Boolean) object);
        } else if (object instanceof Integer) {
            localStr = String.valueOf((Integer) object);
        } else if (object instanceof Long) {
            localStr = String.valueOf((Long) object);
        } else if (object instanceof Double) {
            localStr = String.valueOf((Double) object);
        } else if (object instanceof String) {
            localStr = (String) object;
        } else if (object instanceof java.util.Date) {
            localStr = String.valueOf(dateFormat.format((java.util.Date) object));
        } else {
            localStr = String.valueOf(object.toString());
        }

        return localStr;
    }
    private static java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
    //private static Comparator fieldOrderComparable = new FieldOrderComparator();

    /**
     * @return the maxRows
     */
    public static int getMaxRows() {
        return maxRows;
    }

    /**
     * @param aMaxRows the maxRows to set
     */
    public static void setMaxRows(int aMaxRows) {
        maxRows = aMaxRows;
    }
}//class-end