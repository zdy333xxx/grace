/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zdy.base.controller;

import com.zdy.base.util.RelationItemsComparator;
import com.zdy.base.util.RelationListComparator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
public class TestController {

    @RequestMapping("/test")
    @ResponseBody
    protected Map<String, ?> handTestRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        return getData();
    }

    public Map<String, Object> getData() {

        nameN = 0;
        Integer pageTotal = 2;

        List<Map<String, Object>> pageItemsList = getPageItems(pageTotal);

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("name", "中心人");
        dataMap.put("zjhm", "000000000000000000");
        dataMap.put("pageTotal", pageTotal);
        dataMap.put("pageList", pageItemsList);

        return dataMap;
    }

    public List<Map<String, Object>> getPageItems(Integer pageTotal) {

        List<Map<String, Object>> pageItemsList = new ArrayList<>();

        for (int i = 0; i < pageTotal; i++) {

            Integer itemsOfPage = 50;
            List<Map<String, Object>> relationList = getRelationList(itemsOfPage);

            Map<String, Object> pageItemMap = new HashMap<>();
            pageItemMap.put("pageIndex", i + 1);
            pageItemMap.put("pageItemList", relationList);

            pageItemsList.add(pageItemMap);
        }

        return pageItemsList;
    }

    public List<Map<String, Object>> getRelationList(Integer itemsOfPage) {

        String initZJHM = "000000000000000000";
        List<Map<String, Object>> relationList = new ArrayList<>();

        Map<String, Integer> weightCountMap = new HashMap<>();
        weightCountMap.put("weightCount", 0);

        for (int i = 0; i < itemsOfPage; i++) {

            List<Map<String, Object>> relationItemsList = getRelationItems(weightCountMap);

            //nameN++;
            Map<String, Object> relationMap = new HashMap<>();
            //relationMap.put("name", "张三" + nameN);
            relationMap.put("zjhm", initZJHM.replaceAll("0", String.valueOf(1)));
            relationMap.put("relationItems", relationItemsList);
            relationMap.put("weightCount", weightCountMap.get("weightCount"));
            relationMap.put("updateTime", updateTimeStr);

            relationList.add(relationMap);

            weightCountMap.put("weightCount", 0);
        }

        //逆序排序
        Collections.sort(relationList, new RelationListComparator());

        //设置关系人名称
        for (Map<String, Object> relationMap : relationList) {
            nameN++;
            relationMap.put("name", "关系人" + nameN);
        }

        return relationList;
    }

    public List<Map<String, Object>> getRelationItems(Map<String, Integer> weightCountMap) {

        List<Map<String, Object>> relationItemsList = new ArrayList<>();

        Set<Integer> weightSet = new HashSet<>();

        int m = (int) (1 + Math.random() * (5));
        while (m > 0) {
            int weight = (int) (1 + Math.random() * (14));
            if (weightSet.contains(weight)) {
                continue;
            }
            weightSet.add(weight);

            Map<String, Object> relationItemMap = new HashMap<>();
            relationItemMap.put("name", relationDefineMap.get(weight));
            relationItemMap.put("weight", weight);

            relationItemsList.add(relationItemMap);

            weightCountMap.put("weightCount", weightCountMap.get("weightCount") + weight);

            m--;
        }

        //逆序排序
        Collections.sort(relationItemsList, new RelationItemsComparator());
        return relationItemsList;
    }

    //-------------------------------------------------
    private static int nameN = 0;

    private static final Map<Integer, String> relationDefineMap = new TreeMap<>();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final Date date = new Date();
    private static String updateTimeStr = null;

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

        updateTimeStr = dateFormat.format(date);
    }

}
