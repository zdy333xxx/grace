package com.hzcominfo.aggr.service;

import com.hzcominfo.aggr.dao.QueryAggrInfoDao2;
import com.hzcominfo.auth.model.AuthUserContext;
import java.util.Iterator;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import com.mongodb.DB;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

/*
 * @author zdy
 */
public class QueryAggrInfoService {

    /*
     * 数据查询服务的入口方法
     */
    public static JsonNode service(DB dbConfig, DB dbData, AuthUserContext modelContext, String strBody) {
        ObjectNode rootNode = JsonNodeFactory.instance.objectNode(); // 根节点
        if (strBody == null) {
            rootNode.put("resultCode", -1);
            rootNode.put("resultMsg", "出错了，未能获取请求参数");

            System.out.println(rootNode);
            return rootNode;
        }

        if (modelContext == null) {
            rootNode.put("resultCode", 0);
            rootNode.put("resultMsg", "查询失败，用户未登陆");

            //System.out.println(rootNode);
            return rootNode;
        }

        JsonNode localValueNode = null;

        try {
            ObjectMapper localMapper = new ObjectMapper();
            JsonNode requestRootNode = localMapper.readTree(strBody);

            if (requestRootNode == null) {
                rootNode.put("resultCode", -1);
                rootNode.put("resultMsg", "出错了，未能获取请求参数");

                System.out.println(rootNode);
                return rootNode;
            }

            localValueNode = requestRootNode.get("func"); //获取操作类型参数
            if (localValueNode == null) {
                rootNode.put("resultCode", 0);
                rootNode.put("resultMsg", "查询失败，功能名称未定义：" + null);

                System.out.println(rootNode);
                return rootNode;
            }
            String funcParam = localValueNode.getValueAsText();

            if (!"query".equals(funcParam)) {
                rootNode.put("resultCode", 0);
                rootNode.put("resultMsg", "查询失败，功能名称未定义：" + funcParam);

                System.out.println(rootNode);
                return rootNode;
            }

            localValueNode = requestRootNode.get("requestId");//获取查询类型参数
            if (localValueNode == null) {
                rootNode.put("resultCode", 0);
                rootNode.put("resultMsg", "查询失败，请求名称未定义：" + null);

                System.out.println(rootNode);
                return rootNode;
            }
            String requestIdParam = localValueNode.getValueAsText();

            localValueNode = requestRootNode.get("maxRows");//获取每个数据模型的最大返回记录数
            if (localValueNode != null) {
                int maxRows = localValueNode.getValueAsInt();
                if (maxRows > 0) {
                    QueryAggrInfoDao2.setMaxRows(maxRows); //********************
                }
            }

            JsonNode conditionParamNode = requestRootNode.get("condition");//获取查询条件
            if (conditionParamNode == null) {
                rootNode.put("resultCode", 0);
                rootNode.put("resultMsg", "查询失败，未能获取查询条件：" + null);

                System.out.println(rootNode);
                return rootNode;
            }

            localValueNode = conditionParamNode.get("dimension");//获取查询类型参数
            if (localValueNode == null) {
                rootNode.put("resultCode", 0);
                rootNode.put("resultMsg", "查询失败，数据维度名称未定义：" + null);

                System.out.println(rootNode);
                return rootNode;
            }
            String dimensionParam = localValueNode.getValueAsText();

            Map<String, String> paramMap = new HashMap<String, String>();

            JsonNode paramArrNode = conditionParamNode.get("param");//获取查询参数
            if (paramArrNode == null) {
                rootNode.put("resultCode", 0);
                rootNode.put("resultMsg", "查询失败，未能获取查询参数" + paramArrNode);

                System.out.println(rootNode);
                return rootNode;
            }

            int paramSize = paramArrNode.size();
            if (paramSize < 1) {
                rootNode.put("resultCode", 0);
                rootNode.put("resultMsg", "查询失败，未能获取查询参数" + paramArrNode);

                System.out.println(rootNode);
                return rootNode;
            }
            for (int i = 0; i < paramSize; i++) {
                JsonNode paramNode = paramArrNode.get(i);
                Iterator<String> it = paramNode.getFieldNames();
                while (it.hasNext()) {
                    String paramFieldName = it.next();
                    String paramValue = null;
                    localValueNode = paramNode.get(paramFieldName);
                    if (localValueNode != null) {
                        paramValue = localValueNode.getValueAsText();
                    }
                    if (paramValue != null && !(paramValue.trim().isEmpty()) && !("*".equals(paramValue))) {
                        paramMap.put(paramFieldName, paramValue);
                    }
                }
            }

            if (paramMap.isEmpty()) {
                rootNode.put("resultCode", 0);
                rootNode.put("resultMsg", "查询失败，未能获取有效的查询参数");

                System.out.println(rootNode);
                return rootNode;
            }


            Date startTime1 = new Date();

            //获取fields节点           
            ArrayNode fieldsNode = JsonNodeFactory.instance.arrayNode();
            JsonNode fieldsOfDimensionNode = QueryAggrInfoDao2.getFields(dbConfig, dimensionParam);
            fieldsNode.add(fieldsOfDimensionNode);

            Date endTime1 = new Date();
            System.out.println("\n\n获取字段列表花费时长-->" + (double) (endTime1.getTime() - startTime1.getTime()) / 1000 + "S");
            System.out.println("------------------------------------------------------------------------------------------");

            //获取rows节点 
            ArrayNode rowsNode = JsonNodeFactory.instance.arrayNode();
            JsonNode rowsOfDimensionNode = QueryAggrInfoDao2.getRows(modelContext, dbConfig, dbData, dimensionParam, paramMap);
            rowsNode.add(rowsOfDimensionNode);

            Date endTime2 = new Date();
            System.out.println("此次查询总共花费时长-->" + (double) (endTime2.getTime() - startTime1.getTime()) / 1000 + "S\n");

            rootNode.put("resultCode", 1);
            rootNode.put("resultMsg", "查询成功");
            rootNode.put("requestId", requestIdParam);
            rootNode.put("fields", fieldsNode);
            rootNode.put("rows", rowsNode);

        } catch (Exception ex) {
            Logger.getLogger(QueryAggrInfoService.class.getName()).log(Level.SEVERE, null, ex);

            rootNode.put("resultCode", -1);
            rootNode.put("resultMsg", "出错了,输入参数字符串不能正常解析 或 数据库访问异常");
            return rootNode;
        }

        return rootNode;
    }

    //private static java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
}
