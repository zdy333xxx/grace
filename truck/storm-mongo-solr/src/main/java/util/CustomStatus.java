package util;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author zdy
 */
public class CustomStatus {

    private static boolean completed = false;
    private static Integer taskCount = 0;

    /**
     * @return the completed
     */
    public static synchronized boolean isCompleted() {
        return completed;
    }

    /**
     * @param aCompleted the completed to set
     */
    public static synchronized void setCompleted(boolean aCompleted) {
        completed = aCompleted;
    }

    /**
     * @return the taskCount
     */
    public static synchronized Integer getTaskCount() {
        return taskCount;
    }

    /**
     * @param aTaskCount the taskCount to set
     */
    public static synchronized void setTaskCount(Integer aTaskCount) {
        taskCount = aTaskCount;
    }

}
