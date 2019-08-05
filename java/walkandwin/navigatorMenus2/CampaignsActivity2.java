package com.boun.volkanyilmaz.walkandwin.navigatorMenus2;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.boun.volkanyilmaz.walkandwin.R;
import com.boun.volkanyilmaz.walkandwin.models.Advertisement;
import com.boun.volkanyilmaz.walkandwin.models.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CampaignsActivity2 extends AppCompatActivity {
  private TextView tx1,tx2,tx3;
  private TextInputLayout tilreklam, tilbaslik;
  private TextInputEditText reklam, baslik;
  private FirebaseDatabase database;
  private DatabaseReference ref;
  private FirebaseAuth auth;
  private static UserData userData;
  private static String adv,title;
  private static int advnumber = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_campaigns2);
    androidTools();
    auth = FirebaseAuth.getInstance();
    database = FirebaseDatabase.getInstance();
    ref = database.getReferenceFromUrl("https://yourfirebaseadress/users");
    fetchAdvertisements();

  }

  @Override
  protected void onStart() {
    super.onStart();

  }

  public void fetchAdvertisements() {
    if (auth.getInstance().getUid() != null){
      ref.child(auth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
          if (dataSnapshot.exists()) {
            userData = dataSnapshot.getValue(UserData.class);
            List<Advertisement> advs = new ArrayList<>();
            if (userData != null && userData.advertisement != null) {
              for (Advertisement a : userData.advertisement) {
                if (a != null) {
                  advs.add(a);
                  advnumber++; // TODO what is that
                }
              }
            }
            tx1.setText(advs.get(0).getTitle());
            tx2.setText(advs.get(1).getTitle());
            tx3.setText(advs.get(2).getTitle());
          }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
          Toast.makeText(CampaignsActivity2.this, "Something went wrong while fetching data from database.", Toast.LENGTH_SHORT).show();
        }
      });
    }

  }

  public void click(View v) {
    switch (v.getId()) {
      case R.id.publish:
        boolean durumBaslik = TextUtils.isEmpty(baslik.getText());
        boolean durumReklam = TextUtils.isEmpty(reklam.getText());
        tilbaslik.setError(null);
        tilreklam.setError(null);
        if (durumBaslik){
          tilreklam.setError("Lütfen bir başlık giriniz.");
        }
        else if (durumReklam){
          tilreklam.setError("Lütfen bir reklam kampanyası paylaşınız.");
        }
        else {
          if(advnumber<4){
            adv = reklam.getText().toString();
            title = baslik.getText().toString();
            reklam.setText(adv);
            baslik.setText(title);
            ref.child(auth.getUid()).child("advertisement").child(String.valueOf(advnumber)).child("title").setValue(title);
            ref.child(auth.getUid()).child("advertisement").child(String.valueOf(advnumber)).child("adv").setValue(adv);
            Toast.makeText(CampaignsActivity2.this, "Reklam başarıyla yayınlandı.", Toast.LENGTH_SHORT).show();
            advnumber++;
          }
          else{

            AlertDialog.Builder builder = new AlertDialog.Builder(CampaignsActivity2.this);

            builder
              .setMessage("En fazla 3 tane reklam yayınlabilirsiniz. Mevcut reklamınızı değiştirmek ister misiniz?")
              .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  advnumber = 1;
                  adv = reklam.getText().toString();
                  title = baslik.getText().toString();
                  ref.child(auth.getUid()).child("advertisement").child(String.valueOf(advnumber)).child("title").setValue(title);
                  ref.child(auth.getUid()).child("advertisement").child(String.valueOf(advnumber)).child("adv").setValue(adv);
                  Toast.makeText(CampaignsActivity2.this, "Reklam başarıyla değiştirildi.", Toast.LENGTH_SHORT).show();
                  advnumber++;
                }
              })
              .setNegativeButton("Hayır", null)
              .create();
              builder.show();

          }

        }
        break;
    }
  }

  public void androidTools() {
    tilreklam = findViewById(R.id.tilreklam);
    reklam = findViewById(R.id.reklam);
    tilbaslik = findViewById(R.id.tilbaslik);
    baslik = findViewById(R.id.baslik);
    tx1 = findViewById(R.id.tx1);
    tx2 = findViewById(R.id.tx2);
    tx3 = findViewById(R.id.tx3);
  }
}
