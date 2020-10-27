package com.example.pruebamenu.Models;

public class itemModel{
    String id, nombre, ingred;
    double precio;
    boolean estatus;

    public void itemModel() {
    }

    public itemModel(String id, String nombre, String ingred, double precio, boolean estatus) {
        this.id = id;
        this.nombre = nombre;
        this.ingred = ingred;
        this.precio = precio;
        this.estatus = estatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIngred() {
        return ingred;
    }

    public void setIngred(String ingred) {
        this.ingred = ingred;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public boolean isEstatus() {
        return estatus;
    }

    public void setEstatus(boolean estatus) {
        this.estatus = estatus;
    }
}