package com.kadek.tripgo;

public class Product {


    public String image;
    public String place;
    public String price;
    public String name;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Product() {

    }

    public Product(String image, String place, String price, String name) {

        this.image = image;
        this.place = place;
        this.price = price;
        this.name = name;
    }
}

