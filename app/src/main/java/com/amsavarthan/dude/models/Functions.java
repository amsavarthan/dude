package com.amsavarthan.dude.models;

public class Functions {

    private String title,subtitle;
    private int id;

    public Functions(String title,String subtitle,int id){
        this.subtitle=subtitle;
        this.title=title;
        this.id=id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
