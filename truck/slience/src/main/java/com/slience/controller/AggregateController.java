/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slience.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
@RequestMapping("aggregate")
public class AggregateController {

    @RequestMapping(value = "{dimensionId}/{zjhm}", method = RequestMethod.GET)
    @ResponseBody
    public List<?> handQueryAggregateInfoByDimensionRequest(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("dimensionId") int dimensionId, @PathVariable("zjhm") String zjhm) throws Exception {

        response.setContentType("application/json;charset=UTF-8");

        return null;
    }

    @RequestMapping(value = "{dimensionId}/{attrSetId}/{zjhm}", method = RequestMethod.GET)
    @ResponseBody
    public List<?> handQueryAggregateInfoByAttrSetRequest(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("dimensionId") int dimensionId, @PathVariable("attrSetId") int attrSetId,
            @PathVariable("zjhm") String zjhm) throws Exception {

        response.setContentType("application/json;charset=UTF-8");

        return null;
    }

    @RequestMapping(value = "{dimensionId}/{attrSetId}/{attrGroupId}{zjhm}", method = RequestMethod.GET)
    @ResponseBody
    public List<?> handQueryAggregateInfoByAttrGroupRequest(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("dimensionId") int dimensionId, @PathVariable("attrSetId") int attrSetId,
            @PathVariable("attrGroupId") int attrGroupId, @PathVariable("zjhm") String zjhm) throws Exception {

        response.setContentType("application/json;charset=UTF-8");

        return null;
    }

    @RequestMapping(value = "{dimensionId}/{attrSetId}/{attrArraryId}{zjhm}", method = RequestMethod.GET)
    @ResponseBody
    public List<?> handQueryAggregateInfoByAttrArraryRequest(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("dimensionId") int dimensionId, @PathVariable("attrSetId") int attrSetId,
            @PathVariable("attrArraryId") int attrArraryId, @PathVariable("zjhm") String zjhm) throws Exception {

        response.setContentType("application/json;charset=UTF-8");

        return null;
    }

}
