package com.boun.volkanyilmaz.walkandwin;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.boun.volkanyilmaz.walkandwin.models.Items;
import com.boun.volkanyilmaz.walkandwin.models.UserData;
import com.boun.volkanyilmaz.walkandwin.models.UserMarkerWrapper;
import com.boun.volkanyilmaz.walkandwin.navigatorMenus2.CustomList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyMenuActivity1 extends AppCompatActivity {
  private UserMarkerWrapper myWrapper;
  private ListView lv;
  private CustomList list = new CustomList();
  private ArrayAdapter<String> adapter;
  private int myPrice;
  private FirebaseDatabase database;
  private DatabaseReference ref;
  private FirebaseAuth auth;
  private static UserData userData;
  private int score = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_mymenu1);
    auth = FirebaseAuth.getInstance();
    database = FirebaseDatabase.getInstance();
    ref = database.getReferenceFromUrl("https://yourfirebaseadress/users");
    fetchScore();

    lv = findViewById(R.id.lv1);
    myWrapper = MapsActivity.getMyWrapper();
    for (Items i : myWrapper.userData.getMenu()) {
      if (i != null) {
        list.save(i.getName(), i.getPrice());
      }
    }
    adapter = new ArrayAdapter<>(MyMenuActivity1.this, android.R.layout.simple_list_item_1, list.getNames());
    lv.setAdapter(adapter);

    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {  // i is position
        AlertDialog.Builder builder = new AlertDialog.Builder(MyMenuActivity1.this);
        builder
          .setMessage("Bu ögeyi almak istediğinize emin misiniz?")
          .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              myPrice = list.getItems().get(i).getPrice();
              if(score >= myPrice){
                score  = score-myPrice;
                ref.child(auth.getUid()).child("score").setValue(score);
                Toast.makeText(MyMenuActivity1.this, "İtem başarıyla sipariş edildi." + score, Toast.LENGTH_SHORT).show();
              }
              else{
                Toast.makeText(MyMenuActivity1.this, "Bu itemi almak için yeterli puanınız yok. Daha fazla yüreyerek bu ürünü alabilirsiniz." + score, Toast.LENGTH_SHORT).show();
              }

            }
          })
          .setNegativeButton("Hayır", null)
          .create();
        builder.show();
      }
    });
  }
  public void fetchScore() {
    if (auth.getInstance().getUid() != null){
      ref.child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
          if (dataSnapshot.exists()) {
            userData = dataSnapshot.getValue(UserData.class);
            if (userData != null && userData.score != null) {
              score = userData.score;
            }
          }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
          Toast.makeText(MyMenuActivity1.this, "Something went wrong while fetching data from database.", Toast.LENGTH_SHORT).show();
        }
      });
    }
  }
}
