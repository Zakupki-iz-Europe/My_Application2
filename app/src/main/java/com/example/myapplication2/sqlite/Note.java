package com.example.myapplication2.sqlite;

public class Note {
    public static final String TABLE_NAME = "mytable";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ZAK = "zak";
    public static final String COLUMN_DATE = "data";
    public static final String COLUMN_CHAS = "chas";

    private int id;
    private String zak;
    private String data;
    private double chas;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_ZAK + " TEXT,"
                    + COLUMN_DATE + " TEXT,"
                    + COLUMN_CHAS + " DOUBLE"
                    + ")";

    public Note() {
    }

    public Note(int id, String zak, String data, double chas) {
        this.id = id;
        this.zak = zak;
        this.data = data;
        this.chas = chas;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getZak() {
        return zak;
    }

    public void setZak(String zak) {
        this.zak = zak;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public double getChas() {return chas;}

    public void setChas(double chas) {this.chas = chas; }
}
