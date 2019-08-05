package com.boun.volkanyilmaz.walkandwin;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.boun.volkanyilmaz.walkandwin.models.UserData;
import com.boun.volkanyilmaz.walkandwin.models.UserType;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity { //GİRİŞ EKRANI activity_login1

  private FirebaseAuth mAuth;
  private FirebaseDatabase database;
  private DatabaseReference ref;
  private TextInputLayout tilmail, tilpassword;
  private TextInputEditText mail, password;
  private CheckBox checkBox;
  private SharedPreferences loginPreferences;
  private SharedPreferences.Editor loginPrefsEditor;
  private Boolean saveLogin;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login1);
    androidTools();
    mAuth = FirebaseAuth.getInstance();
    database = FirebaseDatabase.getInstance();
    ref = database.getReferenceFromUrl("https://yourfirebaseadress");
    animations();
    loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
    loginPrefsEditor = loginPreferences.edit();

    saveLogin = loginPreferences.getBoolean("saveLogin", false);
    if (saveLogin == true) {
      mail.setText(loginPreferences.getString("mail", ""));
      password.setText(loginPreferences.getString("password", ""));
      checkBox.setChecked(true);
    }

    if (mAuth.getCurrentUser() != null) {// already signed in
      startActivity(new Intent(LoginActivity.this, MapsActivity.class));
      LoginActivity.this.finish();
    }
  }

  public void androidTools() {
    tilmail = findViewById(R.id.tilmailadresi);
    tilpassword = findViewById(R.id.tilpasswordgiris);

    mail = findViewById(R.id.mailadresi);
    password = findViewById(R.id.passwordgiris);

    checkBox = findViewById(R.id.isRemember);
  }

  public void animations() {
    ImageView image = findViewById(R.id.imageLogin);
    Window window = getWindow();
    window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    WindowManager wm = window.getWindowManager();
    Display screen = wm.getDefaultDisplay();
    Point point = new Point();
    screen.getSize(point);

    int genis = point.x;
    int yuksek = point.y;

    image.getLayoutParams().width = (int) (yuksek * 1.39);
    image.getLayoutParams().height = yuksek;

    ObjectAnimator animator = ObjectAnimator.ofFloat(image, "x", 0, -(yuksek * 1.39f - genis), 0, -(yuksek * 1.39f - genis));
    animator.setDuration(210000);
    animator.setInterpolator(new LinearInterpolator());
    animator.start();
  }

  public void click(View v) {
    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(mail.getWindowToken(), 0);

    switch (v.getId()) {
      case R.id.login1:
        boolean durummail = TextUtils.isEmpty(mail.getText());
        boolean durumpassword = TextUtils.isEmpty(password.getText());

        tilmail.setError(null);
        tilpassword.setError(null); //hata gidince yazı kalksın

        if (durummail || durumpassword || !mail.getText().toString().contains("@")) { //içermediği durum
          if (durummail)
            tilmail.setError("Lütfen mail adresinizi giriniz.");
          else if (!mail.getText().toString().contains("@"))
            tilmail.setError("Lütfen geçerli bir mail giriniz.");
          else if (durumpassword)
            tilpassword.setError("Lütfen bir şifre giriniz.");
        }
        else { // not signed in
          mAuth.signInWithEmailAndPassword(mail.getText().toString(), password.getText().toString())
            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
              @Override
              public void onSuccess(AuthResult authResult) {
                ref.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                  @Override
                  public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                      UserData userData = dataSnapshot.getValue(UserData.class);
                      if (userData.userType == UserType.INDIVIDUAL) {
                        startActivity(new Intent(LoginActivity.this, MapsActivity.class));
                      } else {
                        Toast.makeText(LoginActivity.this, "Kurumsal giriş ekranından giriş yapınız.", Toast.LENGTH_SHORT).show();
                      }
                    } else {
                      Toast.makeText(LoginActivity.this, "Something went wrong while fetching data from database.", Toast.LENGTH_SHORT).show();
                    }
                  }

                  @Override
                  public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(LoginActivity.this, "Something went wrong about database.", Toast.LENGTH_SHORT).show();
                  }
                });
              }
            })
            .addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Mail adresi veya şifre yanlış.", Toast.LENGTH_SHORT).show();
              }
            });
        }
        break;
      case R.id.register1:
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        break;
      case R.id.sifremiUnuttum:
        startActivity(new Intent(LoginActivity.this, PasswordActivity.class));
        break;
      case R.id.isRemember:
        if (checkBox.isChecked()) {
          loginPrefsEditor.putBoolean("saveLogin", true);
          loginPrefsEditor.putString("mail", mail.getText().toString());
          loginPrefsEditor.putString("password", password.getText().toString());
          loginPrefsEditor.commit();
        } else {
          loginPrefsEditor.clear();
          loginPrefsEditor.commit();
        }
        break;
    }
  }
}