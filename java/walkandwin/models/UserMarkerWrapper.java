package com.boun.volkanyilmaz.walkandwin.models;

import com.google.android.gms.maps.model.Marker;

public class UserMarkerWrapper {
  public UserData userData;
  public Marker marker;

  public UserMarkerWrapper(UserData userData) {
    this.userData = userData;
  }
}
