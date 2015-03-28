/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zdy.solr.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 *
 * @author zdy
 */
@Service
@Scope("prototype")
public class SolrSimpleQueryService {
    
    public void service(){
        
        System.out.println("hello,spring....................");
        
    }
    
}
