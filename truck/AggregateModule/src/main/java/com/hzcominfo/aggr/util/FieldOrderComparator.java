/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hzcominfo.aggr.util;

import com.mongodb.DBObject;
import java.util.Comparator;

/**
 *
 * @author breeze
 */
public class FieldOrderComparator implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        DBObject doc_1 = (DBObject) o1;
        DBObject doc_2 = (DBObject) o2;

        int fieldOrder_1 = Integer.parseInt(String.valueOf(doc_1.get("field_order")));
        int fieldOrder_2 = Integer.parseInt(String.valueOf(doc_2.get("field_order")));

        return fieldOrder_1 - fieldOrder_2;
    }

}
