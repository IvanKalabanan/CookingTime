package com.anna.cookingtime.models;

/**
 * Created by iva on 21.03.17.
 */

public class Category {
    private long id;
    private String name;
    private String icon_name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon_name;
    }

    public void setIcon(String icon) {
        this.icon_name = icon;
    }
}
