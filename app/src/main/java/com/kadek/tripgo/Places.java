package com.kadek.tripgo;

/**
 * Created by K on 02/03/2018.
 */

public class Places {

    public String name;
    public String image;
    public String price;
    public String latlong;
    public String thumb_image;
    public String description;
    public String phone;
    public String youtube;
    public String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Places(String category) {
        this.category = category;
    }

    public Places() {
    }

    public Places(String name, String price, String thumb_image) {
        this.name = name;
        this.price = price;
        this.thumb_image = thumb_image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }
}
