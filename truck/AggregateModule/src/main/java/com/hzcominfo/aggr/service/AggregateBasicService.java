/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hzcominfo.aggr.service;

import com.hzcominfo.aggr.dao.AggregateBasicDao;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 *
 * @author zdy
 */
@Service
@Scope("prototype")
public class AggregateBasicService {

    @Autowired
    private AggregateBasicDao aggregateBaseDao;

    public void queryAggregateInfoByDimension(Map<String, Object> paramMap) {

    }

    public void queryAggregateInfoByAttrSet(Map<String, Object> paramMap) {

    }

    public void queryAggregateInfoByAttrGroup(Map<String, Object> paramMap) {

    }

    public void queryAggregateInfoByAttrArrary(Map<String, Object> paramMap) {

    }

}
