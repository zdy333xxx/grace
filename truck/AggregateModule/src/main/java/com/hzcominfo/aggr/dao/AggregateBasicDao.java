/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hzcominfo.aggr.dao;

import com.hzcominfo.aggr.util.MongoOpt;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

/**
 *
 * @author zdy
 */
@Repository
@Scope("prototype")
public class AggregateBasicDao {

    private static final String QUERY_DIMENSION_DEFINE_COLL_NAME = "query_dimension_define";
    private static final String QUERY_ATTRSET_DEFINE_COLL_NAME = "query_attrset_define";
    private static final String QUERY_DIMENSION_ATTRSET_COLL_NAME = "query_dimension_attrset";
    private static final String QUERY_ATTRSET_PARAM_COLL_NAME = "query_attrset_param";
    private static final String QUERY_ATTR_DEFINE_COLL_NAME = "query_attr_define";
    private static final String QUERY_ATTR_EXTEND_PARAM_COLL_NAME = "query_attr_extend_param";
    //private static final String QUERY_COL_SYSTEM_MAP_COLL_NAME ="query_col_system_map";
    private static int maxRows = Integer.MAX_VALUE;

    //获取字段列表*************************
    public static Map<String, Object> getFields(DB dbConfig, String dimensionParam) {
        Map<String, Object> fieldsMap = new HashMap<String, Object>();

        DBObject query = new BasicDBObject();
        query.put("query_dimension_name", dimensionParam);

        DBObject fieldDoc = new BasicDBObject();
        fieldDoc.put("query_dimension_display_name", 1);

        DBObject dimensionDefineDoc = MongoOpt.findOne(dbConfig, QUERY_DIMENSION_DEFINE_COLL_NAME, query, fieldDoc);
        if (dimensionDefineDoc == null) {
            return fieldsMap;
        }
        String dimensionDisplayName = (String) dimensionDefineDoc.get("query_dimension_display_name"); //维度显示名称

        //对每个集合查询时的扩展（条件过滤）参数缓存
        query = new BasicDBObject();
        query.put("enable", 1);

        fieldDoc = new BasicDBObject();
        fieldDoc.put("_id", 0);
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

            //extraQueryParamOfAllCollMap.put(collName, extraQueryParamOfCollMap);
        }

        //System.out.println("extraQueryParamOfAllCollMap-->"+extraQueryParamOfAllCollMap);
        //JsonNode attrSetNode = getFieldsOfAttrSetByDimension(dbConfig, dimensionParam);
        fieldsMap.put("dimensionId", dimensionParam);
        fieldsMap.put("dimensionName", dimensionParam);
        fieldsMap.put("dimensionDisplayName", dimensionDisplayName);
        fieldsMap.put("attrSetItems", null);

        return fieldsMap;
    }

}
