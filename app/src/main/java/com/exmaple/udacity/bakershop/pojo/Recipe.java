package com.exmaple.udacity.bakershop.pojo;

public class Recipe {
    private int id;
    private String name;
    private int servings;
    private String imageURL;

    public Recipe() {}

    public Recipe(int id,String name,int servings,String imageURL) {
        this.id=id;
        this.name=name;
        this.servings=servings;
        this.imageURL=imageURL;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }
}
