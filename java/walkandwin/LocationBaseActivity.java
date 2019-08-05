package com.boun.volkanyilmaz.walkandwin;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.boun.volkanyilmaz.walkandwin.utils.PermissionUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationBaseActivity extends AppCompatActivity implements
    ActivityCompat.OnRequestPermissionsResultCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

  private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
  private GoogleApiClient googleApiClient;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    googleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API)
        .build();
  }

  public void connect() {
    if (googleApiClient != null && !googleApiClient.isConnected()
        && !googleApiClient.isConnecting()) {
      googleApiClient.connect();
    }
  }

  public void disconnect() {
    if (googleApiClient != null && (googleApiClient.isConnected()
        || googleApiClient.isConnecting())) {
      googleApiClient.disconnect();
    }
  }


  public boolean checkLocationPermission() {
    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      // Permission to access the location is missing.
      PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
          android.Manifest.permission.ACCESS_FINE_LOCATION, true);
      return false;
    }
    return true;
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (LOCATION_PERMISSION_REQUEST_CODE == requestCode) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        onLocationPermissionOK();
      } else {
        onLocationPermissionFail();
      }
      return;
    }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  public Location getLocation() {
    if (googleApiClient != null && googleApiClient.isConnected()) {
      @SuppressLint("MissingPermission") Location fusedLocation = LocationServices.FusedLocationApi
          .getLastLocation(googleApiClient);
      if (fusedLocation != null) {
        return fusedLocation;
      } else {
        requestLocationFromGoogleApiClient();
      }
    }
    return null;
  }

  @SuppressLint("MissingPermission")
  public void requestLocationFromGoogleApiClient() {
    try {
      LocationRequest locationRequest = LocationRequest.create()
          .setInterval(500)
          .setFastestInterval(0)
          .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
          .setNumUpdates(1);
      LocationServices.FusedLocationApi
          .requestLocationUpdates(googleApiClient, locationRequest, this);
    } catch (Exception e) {
      Toast.makeText(this, "Error Occurred", Toast.LENGTH_SHORT).show();
    }
  }

  public void onLocationPermissionOK() {
    Toast.makeText(this, "Location Permission Success", Toast.LENGTH_SHORT).show();
  }

  public void onLocationPermissionFail() {
    Toast.makeText(this, "Location Permission Fail", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {
    Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onConnectionSuspended(int i) {
    Toast.makeText(this, "Connection Suspended", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onLocationChanged(Location location) {

  }
}
