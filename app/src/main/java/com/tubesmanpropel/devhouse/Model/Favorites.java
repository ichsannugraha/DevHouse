package com.tubesmanpropel.devhouse.Model;

public class Favorites {

    private String pid, sid;

    private Favorites() {
        //Empty Constructor
    }

    public Favorites(String pid, String sid) {
        this.pid = pid;
        this.sid = sid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
