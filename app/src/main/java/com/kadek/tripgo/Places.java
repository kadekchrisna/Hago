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
    public String link;

    public Places() {
    }

    public Places(String name, String image, String price, String latlong, String thumb_image, String description, String phone, String youtube, String category, String link) {
        this.name = name;
        this.image = image;
        this.price = price;
        this.latlong = latlong;
        this.thumb_image = thumb_image;
        this.description = description;
        this.phone = phone;
        this.youtube = youtube;
        this.category = category;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLatlong() {
        return latlong;
    }

    public void setLatlong(String latlong) {
        this.latlong = latlong;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
