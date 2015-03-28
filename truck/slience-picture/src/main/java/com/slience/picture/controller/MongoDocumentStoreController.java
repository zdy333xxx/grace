/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slience.picture.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author zdy
 */
@Controller
//@EnableAutoConfiguration

@RequestMapping("picture")
public class MongoDocumentStoreController {

    @RequestMapping(value = "mongo/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String detectDevice(@PathVariable("id") String id) {

        return "pictrure " + id + " storage in mongodb document..........";
    }

   

}
