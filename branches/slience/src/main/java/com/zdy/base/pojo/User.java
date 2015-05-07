/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zdy.base.pojo;

import java.util.Date;

/**
 *
 * @author breeze
 */
public class User {

    private Long roleId;
    private Long userId;
    private String username;
    private String password;
    private Date add_time;
    private String comment;
    private Integer status;

    public User() {
        this.roleId = null;
        this.userId = null;
        this.username = null;
        this.password = null;
        this.add_time = null;
        this.comment = null;
        this.status = 1;
    }

    /**
     * @return the roleId
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * @param roleId the roleId to set
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    /**
     * @return the userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the add_time
     */
    public Date getAdd_time() {
        return add_time;
    }

    /**
     * @param add_time the add_time to set
     */
    public void setAdd_time(Date add_time) {
        this.add_time = add_time;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

}
