package com.js.academic.crawler;

import java.util.ArrayList;
import java.util.List;

public class MealInfo {

    private String date;
    private String mealType;
    private String price;
    private List<String> menus;
    private boolean available;

    public MealInfo() {
        this.menus = new ArrayList<>();
    }

    public MealInfo(
            String date,
            String mealType,
            String price,
            List<String> menus,
            boolean available) {

        this.date = date;
        this.mealType = mealType;
        this.price = price;
        this.menus = menus;
        this.available = available;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public List<String> getMenus() {
        return menus;
    }

    public void setMenus(List<String> menus) {
        this.menus = menus;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}