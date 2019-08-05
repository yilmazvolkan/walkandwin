package com.boun.volkanyilmaz.walkandwin.navigatorMenus1;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.boun.volkanyilmaz.walkandwin.MapsActivity;
import com.boun.volkanyilmaz.walkandwin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity1 extends AppCompatActivity {
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
    setContentView(R.layout.activity_profile1);

    androidTools();
    writePoint();
    writeInfo();

  }

  public void androidTools() {

    tilname = findViewById(R.id.tilname);
    tilmail = findViewById(R.id.tilmail);

    name = findViewById(R.id.nav_name);
    mail = findViewById(R.id.nav_mail);
  }
  public void writePoint() {
    TextView point = findViewById(R.id.nav_point_text);
    if (MapsActivity.getterPoint() > 0) {
      point.setText(String.valueOf(MapsActivity.getterPoint()));
    } else
      point.setText(String.valueOf(0));
  }

  public void writeInfo() {
    name.setText(String.valueOf(MapsActivity.getterName().toUpperCase()));
    mail.setText(String.valueOf(MapsActivity.getterMail()));
  }

  public void click(View v) {
    switch (v.getId()) {
      case R.id.sifremiunuttum:
        mAuth.sendPasswordResetEmail(mail.getText().toString());
        Toast.makeText(ProfileActivity1.this, "Şifrenizi değiştirmek için e-posta gönderdik.", Toast.LENGTH_SHORT).show();
        break;
      case R.id.nav_name:
        break;
      case R.id.nav_mail:
        break;
      case R.id.kaydet:
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
