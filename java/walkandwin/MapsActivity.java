package com.boun.volkanyilmaz.walkandwin;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.boun.volkanyilmaz.walkandwin.models.Advertisement;
import com.boun.volkanyilmaz.walkandwin.models.UserData;
import com.boun.volkanyilmaz.walkandwin.models.UserMarkerWrapper;
import com.boun.volkanyilmaz.walkandwin.models.UserType;
import com.boun.volkanyilmaz.walkandwin.navigatorMenus1.CampaignsActivity1;
import com.boun.volkanyilmaz.walkandwin.navigatorMenus1.DistancesActivity1;
import com.boun.volkanyilmaz.walkandwin.navigatorMenus1.HowWorksActivity1;
import com.boun.volkanyilmaz.walkandwin.navigatorMenus1.ProfileActivity1;
import com.boun.volkanyilmaz.walkandwin.swipe.AdvCard;
import com.boun.volkanyilmaz.walkandwin.swipe.StarCard;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
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
import com.mindorks.placeholderview.SwipePlaceHolderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapsActivity extends LocationBaseActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, SensorEventListener, StepListener {

  private GoogleMap mMap;
  private SwipePlaceHolderView galleryView;
  private FirebaseDatabase database;
  private DatabaseReference ref;
  private FirebaseAuth auth;
  private static UserData userData;
  private Map<String, UserMarkerWrapper> userDataHashMap;
  private TextView TvSteps;
  private TextView UserPoints;
  private StepDetector simpleStepDetector;
  private SensorManager sensorManager;
  private Sensor accel;
  private static final String TEXT_NUM_STEPS = "Steps: ";
  private int numSteps;
  private Button BtnStart;
  private static int points = 0;
  private static String name;
  private static String mail;
  private GeoFire geoFireIndividuals;
  private GeoFire geoFireCommercials;
  private GeoQuery geoQuery;
  private Location lc;
  private Location lc2;
  private Location lastLocation;
  private static UserMarkerWrapper myWrapper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_mapping);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    galleryView = findViewById(R.id.galleryView);
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
      this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);

    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
      .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
    lc = new Location("start");
    lc.setLatitude(0);
    lc.setLongitude(0);
    lc2 = new Location("end");
    lc2.setLatitude(0);
    lc2.setLongitude(0);
    auth = FirebaseAuth.getInstance();
    database = FirebaseDatabase.getInstance();
    ref = database.getReferenceFromUrl("https://yourfirebaseadress");
    geoFireIndividuals = new GeoFire(database.getReferenceFromUrl("https://yourfirebaseadress/individuals_locations"));
    geoFireCommercials = new GeoFire(database.getReferenceFromUrl("https://yourfirebaseadress/commercials_locations"));
    walkMethod(); // TODO investigate
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
          name = userData.getName();
          mail = userData.getMail();
          if (userData.userType == UserType.INDIVIDUAL) {
            fetchCommercials();
          }
        } else {
          Toast.makeText(MapsActivity.this, "Something went wrong while fetching data from database.", Toast.LENGTH_SHORT).show();
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Toast.makeText(MapsActivity.this, "Something went wrong about database.", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(MapsActivity.this, "Something went wrong while fetching data from database.", Toast.LENGTH_SHORT).show();
      }
    });
  }

  public void addMarker(UserMarkerWrapper userMarkerWrapper) { // For commercial
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
    addMarker(userMarkerWrapper);
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
          MapsActivity.this, R.raw.style_json));

      mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
          if (userDataHashMap != null) {
            Iterator<Map.Entry<String, UserMarkerWrapper>> iterator = userDataHashMap.entrySet().iterator();
            UserMarkerWrapper wrapper = null;
            while (iterator.hasNext() && wrapper == null) {
              Map.Entry<String, UserMarkerWrapper> tmp = iterator.next();
              if (tmp.getValue().marker.equals(marker)) {
                wrapper = tmp.getValue();
              }
            }
            if (wrapper != null) {
              handleMarkerClick(wrapper);
              myWrapper = wrapper;
              return true;
            }
            return false;
          } else {
            Toast.makeText(MapsActivity.this, "Userdatamap yüklenemedi.", Toast.LENGTH_SHORT).show();
            return false;
          }
        }
      });
      if (!success) {
        Log.e("Map", "Style parsing failed.");
      }
    } catch (Resources.NotFoundException e) {
      Log.e("Map", "Can't find style.", e);
    }
  }

  public void handleMarkerClick(UserMarkerWrapper wrapper) {
    startActivity(new Intent(MapsActivity.this, MyMenuActivity1.class));
  }


  private void showMarker(Location location) {

    LatLng current = new LatLng(location.getLatitude(), location.getLongitude());

    mMap.addMarker(new MarkerOptions().position(current).title("Ben").zIndex(-10000000));
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
    lastLocation = location;
    com.boun.volkanyilmaz.walkandwin.models.Location loc = new com.boun.volkanyilmaz.walkandwin.models.Location();
    loc.lat = location.getLatitude();
    loc.lon = location.getLongitude();
    ref.child(auth.getUid()).child("location").setValue(loc);
    geoFireIndividuals.setLocation(auth.getUid(), new GeoLocation(loc.lat, loc.lon));
    mMap.clear();
    showMarker(location);
    fetchCommercials(); //TODO sorun
    checkLocation(loc);
  }


  private void checkLocation(final com.boun.volkanyilmaz.walkandwin.models.Location loc) {
    if (geoQuery != null) {
      geoQuery.removeAllListeners();
    }
    geoQuery = geoFireCommercials.queryAtLocation(new GeoLocation(loc.lat, loc.lon), 0.01);


    geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
      @Override
      public void onKeyEntered(String key, GeoLocation location) {
        lc2.setLatitude(lastLocation.getLatitude());
        lc2.setLongitude(lastLocation.getLongitude());
        handleMessages(key);
      }

      @Override
      public void onKeyExited(String key) {
        Log.e("", "1");
      }

      @Override
      public void onKeyMoved(String key, GeoLocation location) {
        Log.e("", "2");
      }

      @Override
      public void onGeoQueryReady() {
        Log.e("", "3");
      }

      @Override
      public void onGeoQueryError(DatabaseError error) {
        Log.e("", "4");
      }
    });
  }

  public void handleMessages(String key) {
    ref.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
          UserData userData = dataSnapshot.getValue(UserData.class);
          List<Advertisement> advs = new ArrayList<>();
          if (userData != null && userData.advertisement != null) {
            for (Advertisement a : userData.advertisement) {
              if (a != null) {
                advs.add(a);
              }
            }
            showSwipeView(advs);
          }
        } else {
          Toast.makeText(MapsActivity.this, "Something went wrong while fetching data from database.", Toast.LENGTH_SHORT).show();
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Toast.makeText(MapsActivity.this, "Something went wrong about database.", Toast.LENGTH_SHORT).show();
      }
    });
  }

  public void showSwipeView(List<Advertisement> adv) {
    if (adv == null || adv.isEmpty()) {
      return;
    }
    galleryView
      .addView(new StarCard(this.getApplicationContext(), galleryView, userData));
    for (Advertisement advs : adv) {
      galleryView
        .addView(new AdvCard(this.getApplicationContext(), galleryView, advs));
    }
  }

  public void toggleSwipeView() {
    if (galleryView.getVisibility() == View.VISIBLE) {
      galleryView.setVisibility(View.GONE);
    } else {
      galleryView.setVisibility(View.VISIBLE);
    }
  }

  public void click(View v) { // To save start location
    switch (v.getId()) {
      case R.id.walk:
        lc.setLatitude(lastLocation.getLatitude());
        lc.setLongitude(lastLocation.getLongitude());
        float b = distance();
        break;
    }
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    switch (item.getItemId()) {
      case R.id.nav_profile1:
        startActivity(new Intent(MapsActivity.this, ProfileActivity1.class));
        MapsActivity.this.finish();
        break;
      case R.id.nav_campaign1:
        startActivity(new Intent(MapsActivity.this, CampaignsActivity1.class));
        MapsActivity.this.finish();
        break;
      case R.id.nav_distance1:
        startActivity(new Intent(MapsActivity.this, DistancesActivity1.class));
        MapsActivity.this.finish();
        break;
      case R.id.nav_howwork1:
        startActivity(new Intent(MapsActivity.this, HowWorksActivity1.class));
        MapsActivity.this.finish();
        break;
      case R.id.nav_signout1:
        AuthUI.getInstance()
          .signOut(this)
          .addOnCompleteListener(new OnCompleteListener<Void>() {
            public void onComplete(@NonNull Task<Void> task) {
              // user is now signed out
              startActivity(new Intent(MapsActivity.this, ChoseActivity.class));
              MapsActivity.this.finish();
            }
          });
        //auth.signOut();
        break;
    }
    return true;
  }

  private float distance() {

    float[] results = new float[1];
    Location.distanceBetween(lc2.getLatitude(), lc2.getLongitude(), lc.getLatitude(), lc.getLongitude(), results);


    float distanceInMeters = results[0];
    return distanceInMeters;
  }

  public void getPoints(LatLng current, UserMarkerWrapper userMarkerWrapper) {
    if (userMarkerWrapper.userData.location.lon == current.longitude
      && userMarkerWrapper.userData.location.lat == current.latitude) {
      //Puan ata
    }
  }

  public void walkMethod() {
    // Get an instance of the SensorManager
    sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    simpleStepDetector = new StepDetector();
    simpleStepDetector.registerListener(this);

    TvSteps = findViewById(R.id.adim);
    BtnStart = findViewById(R.id.walk);
    // BtnStop = (Button) findViewById(R.id.btn_stop);


    BtnStart.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View arg0) {

        numSteps = 0;
        sensorManager.registerListener(MapsActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);

      }
    });


       /* BtnStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                sensorManager.unregisterListener(MapsActivity.this);

            }
        });*/
  }

  @Override
  public void onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
      simpleStepDetector.updateAccel(
        event.timestamp, event.values[0], event.values[1], event.values[2]);
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int i) {
  }

  @Override
  public void step(long timeNs) {
    numSteps++;
    TvSteps.setText(TEXT_NUM_STEPS + numSteps);
  }

  public static int getterPoint() {
    if (userData.userType == UserType.INDIVIDUAL && userData.score != null) {
      return points;
    }
    return 0;
  }

  public static String getterName() {
    return name;
  }

  public static String getterMail() {
    return mail;
  }

  @Override
  public void onBackPressed() {
    new AlertDialog.Builder(this)
      .setMessage("Are you sure you want to exit?")
      .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          MapsActivity.super.onBackPressed();
          startActivity(new Intent(MapsActivity.this, ChoseActivity.class));
          MapsActivity.this.finish();
        }
      })
      .setNegativeButton("No", null)
      .show();


  }

  public static UserMarkerWrapper getMyWrapper() {
    return myWrapper;
  }

}
