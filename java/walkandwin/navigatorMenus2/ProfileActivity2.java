package com.boun.volkanyilmaz.walkandwin.navigatorMenus2;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.boun.volkanyilmaz.walkandwin.MapsActivity2;
import com.boun.volkanyilmaz.walkandwin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity2 extends AppCompatActivity {
  private FirebaseAuth mAuth;
  private FirebaseDatabase database;
  private DatabaseReference ref;

  private TextInputLayout tilname, tilmail;
  private TextInputEditText name, mail;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mAuth = FirebaseAuth.getInstance();
    database = FirebaseDatabase.getInstance();
    ref = database.getReferenceFromUrl("https://yourfirebaseadress/users");
    setContentView(R.layout.activity_profile2);

    androidTools();
    writePoint();
    writeInfo();

  }

  public void androidTools() {
    Toolbar toolbar = findViewById(R.id.toolbar2);
    setSupportActionBar(toolbar);

    tilname = findViewById(R.id.tilname2);
    tilmail = findViewById(R.id.tilmail2);

    name = findViewById(R.id.nav_name2);
    mail = findViewById(R.id.nav_mail2);
  }

  public void writePoint() {
    TextView point = findViewById(R.id.nav_point_text2);
    if (MapsActivity2.getterPoint2() > 0) {
      point.setText(String.valueOf(MapsActivity2.getterPoint2()));
    } else
      point.setText(String.valueOf(0));
  }

  public void writeInfo() {
    name.setText(String.valueOf(MapsActivity2.getterName2().toUpperCase()));
    mail.setText(String.valueOf(MapsActivity2.getterMail2()));
  }

  public void click(View v) {
    switch (v.getId()) {
      case R.id.sifremiunuttum2:
        mAuth.sendPasswordResetEmail(mail.getText().toString());
        Toast.makeText(ProfileActivity2.this, "Şifrenizi değiştirmek için e-posta gönderdik.", Toast.LENGTH_SHORT).show();
        break;
      case R.id.nav_name2:
        break;
      case R.id.nav_mail2:
        break;
      case R.id.kaydet2:
        boolean durumname = TextUtils.isEmpty(name.getText());
        boolean durummail = TextUtils.isEmpty(mail.getText());
        tilname.setError(null);
        tilmail.setError(null);
        if (durumname)
          tilname.setError("Lütfen ad ve soyad giriniz.");
        else if (durummail)
          tilmail.setError("Lütfen mail adresinizi giriniz.");
        else if (!mail.getText().toString().contains("@"))
          tilmail.setError("Lütfen geçerli bir mail giriniz.");
        else {
          ref.child(mAuth.getUid()).child("fullName").setValue(name.getText().toString());
          ref.child(mAuth.getUid()).child("mail").setValue(mail.getText().toString());
        }
        break;
    }
  }


}
