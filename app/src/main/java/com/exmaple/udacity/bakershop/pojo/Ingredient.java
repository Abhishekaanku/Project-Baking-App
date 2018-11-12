package com.exmaple.udacity.bakershop.pojo;

public class Ingredient {
    private float quantity;
    private String measure;
    private String ingredientName;
    public Ingredient() {}
    public Ingredient(float quantity,String measure,String ingredientName) {
        this.ingredientName=ingredientName;
        this.measure=measure;
        this.quantity=quantity;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }
}
