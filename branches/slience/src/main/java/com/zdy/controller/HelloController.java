/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zdy.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author breeze
 */

@Controller
public class HelloController {
    
    @RequestMapping("/hello.htm")
    protected void handHelloRequest(HttpServletRequest request,HttpServletResponse response){
        
        
        
    }
    
}
