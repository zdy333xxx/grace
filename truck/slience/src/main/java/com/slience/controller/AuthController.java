/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slience.controller;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author zdy
 */
@Controller
@RequestMapping("auth")
public class AuthController {
    
    
    //@Autowired
    //JdbcTemplate jdbcTemplate

    //角色相关------------------------------------
    //查询所有的角色定义信息
    @RequestMapping("role/show/{pageIndex}/{limit}")
    @ResponseBody
    public List<?> handRoleShowRequest(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("pageIndex") int pageIndex,
            @PathVariable("limit") int limit) throws Exception {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        return null;
    }

    //添加角色定义信息
    @RequestMapping("role/add")
    @ResponseBody
    public Map<String, ?> handRoleAddRequest(HttpServletRequest request, HttpServletResponse response,
            @RequestBody Map<String, String> body) throws Exception {

        return null;
    }

    //修改角色定义信息
    @RequestMapping("role/update")
    @ResponseBody
    public Map<String, ?> handRoleUpdateRequest(HttpServletRequest request, HttpServletResponse response,
            @RequestBody Map<String, String> body) throws Exception {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        return null;
    }

    //删除角色定义信息
    @RequestMapping("role/delete/{id}")
    @ResponseBody
    public Map<String, ?> handRoleDeleteRequest(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("id") String id) throws Exception {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        return null;
    }

//用户相关------------------------------------
    @RequestMapping("user/show/{pageIndex}/{limit}")
    @ResponseBody
    public List<?> handUserShowRequest(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("pageIndex") int pageIndex,
            @PathVariable("limit") int limit) throws Exception {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        return null;
    }
    
    @RequestMapping("user/search/{pageIndex}/{limit}")
    @ResponseBody
    public List<?> handUserSearchRequest(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("pageIndex") int pageIndex,
            @PathVariable("limit") int limit) throws Exception {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        return null;
    }

    @RequestMapping("user/add")
    @ResponseBody
    public Map<String, ?> handUserAddRequest(HttpServletRequest request, HttpServletResponse response,
            @RequestBody Map<String, String> body) throws Exception {

        return null;
    }

    @RequestMapping("user/update")
    @ResponseBody
    public Map<String, ?> handUserUpdateRequest(HttpServletRequest request, HttpServletResponse response,
            @RequestBody Map<String, String> body) throws Exception {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        return null;
    }

    @RequestMapping("user/delete/{id}")
    @ResponseBody
    public Map<String, ?> handUserDeleteRequest(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("id") String id) throws Exception {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        return null;
    }

}
