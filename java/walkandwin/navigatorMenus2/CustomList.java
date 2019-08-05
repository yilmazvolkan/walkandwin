package com.boun.volkanyilmaz.walkandwin.navigatorMenus2;

import com.boun.volkanyilmaz.walkandwin.models.Items;

import java.util.ArrayList;

/**
 * Created by volkanyilmaz on 23/02/18.
 */

public class CustomList {
  private ArrayList<Items> items =new ArrayList<>();
  private ArrayList<String> names =new ArrayList<>();

  public void save(String name, int price)
  {
    Items item = new Items(name,price);
    items.add(item);
    names.add(name + " " + price);
  }
  public ArrayList<Items> getItems()
  {
    return items;
  }

  public ArrayList<String> getNames()
  {
    return names;
  }

  public Boolean update(int position,String newName, int newPrice)
  {
    try {
      items.remove(position);
      names.remove(position);
      Items item = new Items(newName,newPrice);
      items.add(position,item);
      names.add(position, newName + " " + newPrice);

      return true;
    }catch (Exception e)
    {
      e.printStackTrace();
      return false;
    }
  }

  public Boolean delete(int position)
  {
    try {
      items.remove(position);
      names.remove(position);
      return true;
    }catch (Exception e)
    {
      e.printStackTrace();
      return false;

    }
  }
}