/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zdy.mybatis.controller;

import com.zdy.mybatis.model.User;
import com.zdy.mybatis.service.UserService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author breeze
 */
@Controller
public class User2Controller {

    //@Resource(name ="userService")
    @Autowired
    @Qualifier("userService")
    private UserService userService;

    @RequestMapping("/user")
    @ResponseBody
    protected Map<String, ?> handUserRequest(HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(value = "id", required = true) long id) throws Exception {
        request.setCharacterEncoding("UTF-8");

        User user = new User();
        user.setApplicantId(id);

        //System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
        User resultUser = userService.doSomething(user);

        Map<String, Object> map = new HashMap<>();

        List<Object> list = new ArrayList<>();
        list.add(resultUser);

        map.put("resultCode", 1);
        map.put("resultMsg", "ok");
        map.put("result", list);

        return map;

        //response.setContentType("text/html;charset=UTF-8");
        //response.getWriter().write(relultStr);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/user/{id}")
    @ResponseBody
    protected Map<String, ?> handUser2Request(HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable long id) throws Exception {
        request.setCharacterEncoding("UTF-8");

        User user = new User();
        user.setApplicantId(id);

        //System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
        User resultUser = userService.doSomething(user);

        Map<String, Object> map = new HashMap<>();

        List<Object> list = new ArrayList<>();
        list.add(resultUser);

        map.put("resultCode", 1);
        map.put("resultMsg", "ok");
        map.put("result", list);

        return map;

        //response.setContentType("text/html;charset=UTF-8");
        //response.getWriter().write(relultStr);
    }

}
