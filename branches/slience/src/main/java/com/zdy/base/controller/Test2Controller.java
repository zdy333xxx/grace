/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zdy.base.controller;

import com.zdy.base.util.RelationValueComparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author breeze
 */
@Controller
public class Test2Controller {

    @RequestMapping("/test2")
    @ResponseBody
    protected Map<String, ?> handTestRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("resultCode", 1);
        resultMap.put("resultMsg", "success");
        resultMap.put("rows", getRows());
        resultMap.put("markRows", getMarkRows());

        return resultMap;
    }

    public List<Map<String, Object>> getRows() {
        List<Map<String, Object>> rowsList = new ArrayList<>();

        int rowsN = 3;
        for (int i = 0; i < rowsN; i++) {

            Map<String, Integer> relationMarkCountMap = new HashMap<>();
            relationMarkCountMap.put("relationMark", 0);

            List<Map<String, Object>> relationValueList = getRelationValue(relationMarkCountMap);

            Map<String, Object> relationItemMap = new HashMap<>();
            relationItemMap.put("personId", "中心人");
            relationItemMap.put("personName", "000000000000000000");
            relationItemMap.put("dstPersonId", "111111111111111111");
            relationItemMap.put("linkFlag", 1);
            relationItemMap.put("relationMark", relationMarkCountMap.get("relationMark"));
            relationItemMap.put("relationValue", relationValueList);
            relationItemMap.put("relationChild", null);

            rowsList.add(relationItemMap);
        }

        //逆序排序
        Collections.sort(rowsList, new RelationValueComparator());
        int n = 1;
        for (Map<String, Object> relationItemMap : rowsList) {
            relationItemMap.put("dstPersonName", "关系人" + n);
            n++;
        }

        return rowsList;
    }

    public List<Map<String, Object>> getRelationValue(Map<String, Integer> relationMarkCountMap) {
        List<Map<String, Object>> relationValueList = new ArrayList<>();

        Set<Integer> weightSet = new HashSet<>();

        int m = (int) (1 + Math.random() * (5));
        while (m > 0) {
            int weight = (int) (1 + Math.random() * (14));
            if (weightSet.contains(weight)) {
                continue;
            }
            weightSet.add(weight);

            Map<String, Object> relationValueItemMap = new HashMap<>();
            relationValueItemMap.put("relationMark", weight);
            relationValueItemMap.put("relationCode", relationDefineMap.get(weight));
            relationValueItemMap.put("modelName", relationDefineMap.get(weight));

            relationValueList.add(relationValueItemMap);

            relationMarkCountMap.put("relationMark", relationMarkCountMap.get("relationMark") + weight);

            m--;
        }

        //逆序排序
        Collections.sort(relationValueList, new RelationValueComparator());

        return relationValueList;
    }

    //------------------------------------------------------------------------
    public List<Map<String, Object>> getMarkRows() {
        List<Map<String, Object>> markRowsList = new ArrayList<>();

        Map<String, Object> markItemMap = new HashMap<>();
        markItemMap.put("personRouteId", "1,2,3,4");
        markItemMap.put("personMark", "1,2,3,4");
        markItemMap.put("personMarkTotal", 10);

        return markRowsList;
    }

    //-------------------------------------------------
    private static final Map<Integer, String> relationDefineMap = new TreeMap<>();

    static {

        relationDefineMap.put(14, "同户");
        relationDefineMap.put(13, "同事");
        relationDefineMap.put(12, "亲属");
        relationDefineMap.put(11, "同地址");
        relationDefineMap.put(10, "同学");
        relationDefineMap.put(9, "同房间");
        relationDefineMap.put(8, "同入住");
        relationDefineMap.put(7, "同上网");
        relationDefineMap.put(6, "同出行");
        relationDefineMap.put(5, "同航班");
        relationDefineMap.put(4, "同电话");
        relationDefineMap.put(3, "同车");
        relationDefineMap.put(2, "同案");
        relationDefineMap.put(1, "同手段同籍贯");
    }

}
