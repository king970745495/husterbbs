package com.huster.bbs.model;


public class Book {

    private String name;
    private double price;
    private int id;

    public Book(String name, double price, int id) {
        this.name = name;
        this.price = price;
        this.id = id;
    }

    @Override
    public String toString() {
        return "Book{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", id=" + id +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
