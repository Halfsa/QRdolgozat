package com.example.qrdolgozat;

public class Tanulo {
    private int id;
    private String nev;
    private String jegy;

    public Tanulo (int id, String nev, String jegy){
        this.id = id;
        this.nev = nev;
        this.jegy=jegy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNev() {
        return nev;
    }

    public void setNev(String nev) {
        this.nev = nev;
    }

    public String getJegy() {
        return jegy;
    }

    public void setJegy(String jegy) {
        this.jegy = jegy;
    }
}
