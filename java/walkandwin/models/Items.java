package com.boun.volkanyilmaz.walkandwin.models;

/**
 * Created by volkanyilmaz on 23/02/18.
 */

public class Items {

  private String name;
  private int price;

  //CONSTRUCTOR
  public Items() {

  }
  public Items(String name,int price) {
    this.price=price;
    this.name=name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getPrice() {
    return price;
  }

  public String getPrice2() {
    return String.valueOf(price);
  }

  public void setPrice(int price) {
    this.price = price;
  }
}