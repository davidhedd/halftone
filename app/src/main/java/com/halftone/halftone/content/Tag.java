package com.halftone.halftone.content;

/**
 * Created by david on 24/06/2017.
 */

public class Tag {
    private int categoryId;
    private String category;

    public Tag(){}

    public Tag(int categoryId, String category){
        this.categoryId = categoryId;
        this.category = category;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
