package com.amsavarthan.dude.models;

public class CategoryItems {

    private String text,color,name,category_name;

    public CategoryItems(String text,String color,String name,String category_name){
        this.color=color;
        this.text=text;
        this.category_name=category_name;
        this.name=name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }
}
