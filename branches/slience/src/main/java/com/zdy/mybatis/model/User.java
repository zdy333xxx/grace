/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zdy.mybatis.model;

/**
 *
 * @author breeze
 */
public class User {
    
    private long applicantId;
    private String loginName;
    private String password;
    private int status;
    private long addTime;
    private String addComment;
    
    public User(){
        this.applicantId=0;
        this.loginName=null;
        this.password=null;
        this.status=0;
        this.addTime=0;
        this.addComment=null;
    }

    /**
     * @return the applicantId
     */
    public long getApplicantId() {
        return applicantId;
    }

    /**
     * @param applicantId the applicantId to set
     */
    public void setApplicantId(long applicantId) {
        this.applicantId = applicantId;
    }

    /**
     * @return the loginName
     */
    public String getLoginName() {
        return loginName;
    }

    /**
     * @param loginName the loginName to set
     */
    public void setLoginName(String loginName) {
        this.loginName = loginName;
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
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return the addTime
     */
    public long getAddTime() {
        return addTime;
    }

    /**
     * @param addTime the addTime to set
     */
    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    /**
     * @return the addComment
     */
    public String getAddComment() {
        return addComment;
    }

    /**
     * @param addComment the addComment to set
     */
    public void setAddComment(String addComment) {
        this.addComment = addComment;
    }
    
    
    
}
