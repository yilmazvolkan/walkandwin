package com.boun.volkanyilmaz.walkandwin;

import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.boun.volkanyilmaz.walkandwin.models.UserData;
import com.boun.volkanyilmaz.walkandwin.models.UserMarkerWrapper;
import com.boun.volkanyilmaz.walkandwin.models.UserType;
import com.boun.volkanyilmaz.walkandwin.navigatorMenus2.CampaignsActivity2;
import com.boun.volkanyilmaz.walkandwin.navigatorMenus2.HowWorksActivity2;
import com.boun.volkanyilmaz.walkandwin.navigatorMenus2.MyMenuActivity2;
import com.boun.volkanyilmaz.walkandwin.navigatorMenus2.PointActivity2;
import com.boun.volkanyilmaz.walkandwin.navigatorMenus2.ProfileActivity2;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.ui.IconGenerator;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity2 extends LocationBaseActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

  private GoogleMap mMap;
  private FirebaseDatabase database;
  private DatabaseReference ref;
  private FirebaseAuth auth;
  private static UserData userData;
  private Map<String, UserMarkerWrapper> userDataHashMap;
  private TextView TvSteps;
  private static int points = 5;
  private static String name2;
  private static String mail2;
  private GeoFire geoFire;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_mapping2);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = findViewById(R.id.nav_view2);
    navigationView.setNavigationItemSelectedListener(this);

    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map2);
    mapFragment.getMapAsync(this);
    auth = FirebaseAuth.getInstance();
    database = FirebaseDatabase.getInstance();
    ref = database.getReferenceFromUrl("https://yourfirebaseadress");
    geoFire = new GeoFire(database.getReferenceFromUrl("https://yourfirebaseadress/commercials_locations"));
  }

  @Override
  protected void onStart() {
    super.onStart();
    connect();
    ref.child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
          userData = dataSnapshot.getValue(UserData.class);
          points = userData.getScore();
          name2 = userData.getName();
          mail2 = userData.getMail();
        } else {
          Toast.makeText(MapsActivity2.this, "Something went wrong while fetching data from database.", Toast.LENGTH_SHORT).show();
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Toast.makeText(MapsActivity2.this, "Something went wrong about database.", Toast.LENGTH_SHORT).show();
      }
    });
  }

  public void fetchCommercials() {
    userDataHashMap = new HashMap<>();
    ref.orderByChild("userType").equalTo(UserType.COMMERCIAL.name()).addChildEventListener(new ChildEventListener() {
      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        if (dataSnapshot.exists()) {
          String userId = dataSnapshot.getKey();
          UserMarkerWrapper markerWrapper = new UserMarkerWrapper(dataSnapshot.getValue(UserData.class));
          userDataHashMap.put(userId, markerWrapper);
          addMarker(markerWrapper);
        }
      }

      @Override
      public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        if (dataSnapshot.exists()) {
          String userId = dataSnapshot.getKey();
          UserMarkerWrapper markerWrapper = userDataHashMap.get(userId);
          markerWrapper.userData = dataSnapshot.getValue(UserData.class);
          updateMarker(markerWrapper);
        }
      }

      @Override
      public void onChildRemoved(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
          String userId = dataSnapshot.getKey();
          userDataHashMap.remove(userId);
          removeMarker(userId);
        }
      }

      @Override
      public void onChildMoved(DataSnapshot dataSnapshot, String s) {
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Toast.makeText(MapsActivity2.this, "Something went wrong while fetching data from database.", Toast.LENGTH_SHORT).show();
      }
    });
  }

  public void addMarker(UserMarkerWrapper userMarkerWrapper) {
    if (userMarkerWrapper.userData == null) {
      return;
    }
    IconGenerator iconFactory = new IconGenerator(this);
    iconFactory.setTextAppearance(R.style.iconGenText);

    iconFactory.setBackground(getResources().getDrawable(R.drawable.score_marker));

    userMarkerWrapper.marker = mMap.addMarker(new MarkerOptions().
        icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon("+" + userMarkerWrapper.userData.score.toString())))
        .position(new LatLng(userMarkerWrapper.userData.location.lat, userMarkerWrapper.userData.location.lon))
        .title(String.format("%s", userMarkerWrapper.userData.fullName))
        .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV()));
  }

  public void updateMarker(UserMarkerWrapper userMarkerWrapper) {
    userMarkerWrapper.marker.setPosition(new LatLng(userMarkerWrapper.userData.location.lat, userMarkerWrapper.userData.location.lon));
    userMarkerWrapper.marker.setTitle(String.format("%s", userMarkerWrapper.userData.fullName));
    //TODO update these
  }

  public void removeMarker(String userId) {
    UserMarkerWrapper userMarkerWrapper = userDataHashMap.get(userId);
    userDataHashMap.remove(userId);
    userMarkerWrapper.marker.remove();
  }

  @Override
  protected void onStop() {
    super.onStop();
    disconnect();
  }

  /**
   * Manipulates the map once available.
   * This callback is triggered when the map is ready to be used.
   * This is where we can add markers or lines, add listeners or move the camera. In this case,
   * we just add a marker near Sydney, Australia.
   * If Google Play services is not installed on the device, the user will be prompted to install
   * it inside the SupportMapFragment. This method will only be triggered once the user has
   * installed Google Play services and returned to the app.
   */
  @Override
  public void onMapReady(GoogleMap googleMap) {

    mMap = googleMap;
    try {
      // Customise the styling of the base map using a JSON object defined
      // in a raw resource file.
      boolean success = mMap.setMapStyle(
          MapStyleOptions.loadRawResourceStyle(
              MapsActivity2.this, R.raw.style_json));

      if (!success) {
        Log.e("Map", "Style parsing failed.");
      }
    } catch (Resources.NotFoundException e) {
      Log.e("Map", "Can't find style.", e);
    }
  }


  private void showMarker(Location location) {
    LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
    mMap.addMarker(new MarkerOptions().position(current).title("Ben"));
    float zoomLevel = 16.0f; //This goes up to 21 zooming
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, zoomLevel));
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {
    if (checkLocationPermission()) {
      requestLocationFromGoogleApiClient();
    }
  }

  @Override
  public void onLocationPermissionOK() {
    requestLocationFromGoogleApiClient();
  }

  @Override
  public void onLocationChanged(Location location) {
    if (location == null) {
      return;
    }
    com.boun.volkanyilmaz.walkandwin.models.Location loc = new com.boun.volkanyilmaz.walkandwin.models.Location();
    loc.lat = location.getLatitude();
    loc.lon = location.getLongitude();
    ref.child(auth.getUid()).child("location").setValue(loc);
    geoFire.setLocation(auth.getUid(), new GeoLocation(loc.lat , loc.lon));
    showMarker(location);
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    switch (item.getItemId()) {
      case R.id.nav_profile2:
        startActivity(new Intent(MapsActivity2.this, ProfileActivity2.class));
        MapsActivity2.this.finish();
        break;
      case R.id.nav_campaign2:
        startActivity(new Intent(MapsActivity2.this, CampaignsActivity2.class));
        MapsActivity2.this.finish();
        break;
      case R.id.nav_points2:
        startActivity(new Intent(MapsActivity2.this, PointActivity2.class));
        MapsActivity2.this.finish();
        break;
      case R.id.nav_mymenu2:
        startActivity(new Intent(MapsActivity2.this, MyMenuActivity2.class));
        MapsActivity2.this.finish();
        break;
      case R.id.nav_howwork2:
        startActivity(new Intent(MapsActivity2.this, HowWorksActivity2.class));
        MapsActivity2.this.finish();
        break;
      case R.id.nav_signout2:
        auth.signOut();
        /*auth.signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            public void onComplete(@NonNull Task<Void> task) {
              // user is now signed out
              startActivity(new Intent(MapsActivity2.this, LoginActivity2.class));
              finish();
            }
          });*/

        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                public void onComplete(@NonNull Task<Void> task) {
                    // user is now signed out
                    startActivity(new Intent(MapsActivity2.this, ChoseActivity.class));
                    MapsActivity2.this.finish();
                }
            });

        break;
    }
    return true;
  }

  public void getPoints(LatLng current, UserMarkerWrapper userMarkerWrapper) {
    if (userMarkerWrapper.userData.location.lon == current.longitude
        && userMarkerWrapper.userData.location.lat == current.latitude) {
      //Puan ata
    }
  }

  public void click(View v) {
    switch (v.getId()) {
      case R.id.setLoc:
        LatLng current = new LatLng(getLocation().getLatitude(), getLocation().getLongitude());
        break;
    }
  }
  public static int getterPoint2() {
    if (userData.userType == UserType.COMMERCIAL && userData.score != null) {
      return points;
    }
    return 0;
  }
  public static String getterName2() {
    return name2;
  }

  public static String getterMail2() {
    return mail2;
  }


}
