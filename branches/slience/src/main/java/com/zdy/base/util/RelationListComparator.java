/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zdy.base.util;

import java.util.Comparator;
import java.util.Map;

/**
 *
 * @author breeze
 */
public class RelationListComparator implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        Map<String, Object> map1 = (Map<String, Object>) o1;
        Map<String, Object> map2 = (Map<String, Object>) o2;

        Integer weightCount1 = (Integer) map1.get("weightCount");
        Integer weightCount2 = (Integer) map2.get("weightCount");
        
        return weightCount2-weightCount1;
    }

}
