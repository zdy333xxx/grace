/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.zdy.pojo;

/**
 *
 * @author breeze
 */
public class Greeting {

    private final long id;
    private final String content;

    public Greeting(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}
