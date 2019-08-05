package com.boun.volkanyilmaz.walkandwin.models;

import java.util.ArrayList;

public class UserData {

  public String fullName;
  public UserType userType;
  public Integer score;
  public Location location;
  public String mail;
  public ArrayList<Advertisement> advertisement;
  public ArrayList<Items> menu;

  public String getName() {

    return fullName;
  }

  public void setName(String name) {

    this.fullName = name;
  }

  public int getScore() {

    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public String getMail() {

    return mail;
  }

  public void setMail(String mail) {

    this.mail = mail;
  }
  public ArrayList<Items> getMenu() {

    return menu;
  }
}
