package com.example.pruebamenu.Models;

public class cartModel {
    String cart_itemID, cart_itemName, cart_itemDesc, cart_itemNote, cart_negocioID, cart_UserID, cart_userDir, cart_Date;
    double cart_itemPrice, cartItem_newPrice;
    int item_cantidadTotal;
    boolean status;

    public cartModel() {
    }

    public cartModel(String cart_itemID, String cart_itemName, String cart_itemDesc, String cart_itemNote, String cart_negocioID, String cart_UserID, String cart_userDir, String cart_Date, double cart_itemPrice, double cartItem_newPrice, int item_cantidadTotal, boolean status) {
        this.cart_itemID = cart_itemID;
        this.cart_itemName = cart_itemName;
        this.cart_itemDesc = cart_itemDesc;
        this.cart_itemNote = cart_itemNote;
        this.cart_negocioID = cart_negocioID;
        this.cart_UserID = cart_UserID;
        this.cart_userDir = cart_userDir;
        this.cart_Date = cart_Date;
        this.cart_itemPrice = cart_itemPrice;
        this.cartItem_newPrice = cartItem_newPrice;
        this.item_cantidadTotal = item_cantidadTotal;
        this.status = status;
    }

    public String getCart_itemID() {
        return cart_itemID;
    }

    public void setCart_itemID(String cart_itemID) {
        this.cart_itemID = cart_itemID;
    }

    public String getCart_itemName() {
        return cart_itemName;
    }

    public void setCart_itemName(String cart_itemName) {
        this.cart_itemName = cart_itemName;
    }

    public String getCart_itemDesc() {
        return cart_itemDesc;
    }

    public void setCart_itemDesc(String cart_itemDesc) {
        this.cart_itemDesc = cart_itemDesc;
    }

    public String getCart_itemNote() {
        return cart_itemNote;
    }

    public void setCart_itemNote(String cart_itemNote) {
        this.cart_itemNote = cart_itemNote;
    }

    public String getCart_negocioID() {
        return cart_negocioID;
    }

    public void setCart_negocioID(String cart_negocioID) {
        this.cart_negocioID = cart_negocioID;
    }

    public String getCart_UserID() {
        return cart_UserID;
    }

    public void setCart_UserID(String cart_UserID) {
        this.cart_UserID = cart_UserID;
    }

    public String getCart_userDir() {
        return cart_userDir;
    }

    public void setCart_userDir(String cart_userDir) {
        this.cart_userDir = cart_userDir;
    }

    public String getCart_Date() {
        return cart_Date;
    }

    public void setCart_Date(String cart_Date) {
        this.cart_Date = cart_Date;
    }

    public double getCart_itemPrice() {
        return cart_itemPrice;
    }

    public void setCart_itemPrice(double cart_itemPrice) {
        this.cart_itemPrice = cart_itemPrice;
    }

    public double getCartItem_newPrice() {
        return cartItem_newPrice;
    }

    public void setCartItem_newPrice(double cartItem_newPrice) {
        this.cartItem_newPrice = cartItem_newPrice;
    }

    public int getItem_cantidadTotal() {
        return item_cantidadTotal;
    }

    public void setItem_cantidadTotal(int item_cantidadTotal) {
        this.item_cantidadTotal = item_cantidadTotal;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
