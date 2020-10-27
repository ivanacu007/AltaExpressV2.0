package com.example.pruebamenu.Models;

public class pedidoModel {
    String pID, neogioName, pDate, pNota;
    boolean status, expanded;
    double pagoTotal;
    public pedidoModel() {
    }

    public pedidoModel(String pID, String neogioName, String pDate, String pNota, boolean status, boolean expanded, double pagoTotal) {
        this.pID = pID;
        this.neogioName = neogioName;
        this.pDate = pDate;
        this.pNota = pNota;
        this.status = status;
        this.expanded = expanded;
        this.pagoTotal = pagoTotal;
    }

    public String getpID() {
        return pID;
    }

    public void setpID(String pID) {
        this.pID = pID;
    }

    public String getNeogioName() {
        return neogioName;
    }

    public void setNeogioName(String neogioName) {
        this.neogioName = neogioName;
    }

    public String getpDate() {
        return pDate;
    }

    public void setpDate(String pDate) {
        this.pDate = pDate;
    }

    public String getpNota() {
        return pNota;
    }

    public void setpNota(String pNota) {
        this.pNota = pNota;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public double getPagoTotal() {
        return pagoTotal;
    }

    public void setPagoTotal(double pagoTotal) {
        this.pagoTotal = pagoTotal;
    }
}
