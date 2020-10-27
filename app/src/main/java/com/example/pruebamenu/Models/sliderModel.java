package com.example.pruebamenu.Models;

public class sliderModel {
    String id, name, desc, img;
    boolean active;

    public sliderModel() {
    }

    public sliderModel(String id, String name, String desc, String img, boolean active) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.img = img;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
