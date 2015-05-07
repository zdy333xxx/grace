/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zdy.base.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 *
 * @author breeze
 */
public class AuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        System.out.println("\n----------进入用户权限过滤器-------------------------");

        String localContextPath = request.getContextPath();
        String localUri = request.getRequestURI();

        //System.out.println("localContextPath-->" + localContextPath);
        //System.out.println("localUri-->" + localUri);
        String localHtmPath = localUri.substring(localContextPath.length());
        while (localHtmPath.startsWith("/")) {
            localHtmPath = localHtmPath.substring(1);
        }
        while (localHtmPath.endsWith("/")) {
            localHtmPath = localHtmPath.substring(0, localHtmPath.length() - 1);
        }

        //System.out.println("localHtmPath.length-->" + localHtmPath.length());
        //System.out.println("localHtmPath-->" + localHtmPath);
        //免权限过滤的请求地址
        if (localHtmPath.trim().isEmpty() 
                || localHtmPath.compareTo("index") == 0 
                || localHtmPath.compareTo("user/register") == 0
                || localHtmPath.compareTo("user/login") == 0) {
            filterChain.doFilter(request, response);
            return;
        }
        HttpSession localSession = request.getSession();
        if (localSession == null) {
            System.out.println("! Note--->session has closed");
            response.sendRedirect(localContextPath);
            return;
        }

        //获取本次会话中保存的用户信息
        Object localObject = localSession.getAttribute("userInfo");
        if (localObject == null) {
            System.out.println("! Note--->user has not sign in");
            response.sendRedirect(localContextPath);
            return;
        }

        filterChain.doFilter(request, response);
    }

}
