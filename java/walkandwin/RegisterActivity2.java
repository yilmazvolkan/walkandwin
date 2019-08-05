package com.boun.volkanyilmaz.walkandwin;

import android.animation.ObjectAnimator;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.boun.volkanyilmaz.walkandwin.models.UserData;
import com.boun.volkanyilmaz.walkandwin.models.UserType;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity2 extends AppCompatActivity {

  private FirebaseAuth mAuth;
  private FirebaseDatabase database;
  private DatabaseReference ref;
  private TextInputLayout tilkurumadi, tilmail, tilpassword;
  private TextInputEditText kurumadi, mail, password;

  @Override
  protected void onCreate(Bundle savedInstanceState) {  //3. kayıt ekranı  activity_register
    super.onCreate(savedInstanceState);
    mAuth = FirebaseAuth.getInstance();
    animations();
    androidTools();
    database = FirebaseDatabase.getInstance();
    ref = database.getReferenceFromUrl("https://yourfirebaseadress/users");
  }

  public void animations() {
    setContentView(R.layout.activity_register2);
    ImageView image = findViewById(R.id.imageRegister2);
    Window window = getWindow();
    window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    WindowManager wm = window.getWindowManager();
    Display screen = wm.getDefaultDisplay();
    Point point = new Point();
    screen.getSize(point);

    int genis = point.x;
    int yuksek = point.y;

    image.getLayoutParams().width = (int) (yuksek * 1.0);
    image.getLayoutParams().height = yuksek;

    ObjectAnimator animator = ObjectAnimator.ofFloat(image, "x", 0, -(yuksek * 1.0f - genis), 0, -(yuksek * 1.0f - genis));
    animator.setDuration(210000);
    animator.setInterpolator(new LinearInterpolator());
    animator.start();
  }

  public void androidTools() {
    tilkurumadi = findViewById(R.id.tilkurumadikayit2);
    tilmail = findViewById(R.id.tilmailkayit2);
    tilpassword = findViewById(R.id.tilpasswordkayit2);

    kurumadi = findViewById(R.id.kurumadikayit2);
    mail = findViewById(R.id.mailkayit2);
    password = findViewById(R.id.passwordkayit2);
  }

  public void click(View v) {
    switch (v.getId()) {
      case R.id.zatenbirhesabımvar2:
        startActivity(new Intent(RegisterActivity2.this, LoginActivity2.class));
        break;
      case R.id.fabkayit2:
        boolean durumkurumadi = TextUtils.isEmpty(kurumadi.getText());
        boolean durummail = TextUtils.isEmpty(mail.getText());
        boolean durumpassword = TextUtils.isEmpty(password.getText());

        tilkurumadi.setError(null);
        tilmail.setError(null);
        tilpassword.setError(null); //hata gidince yazı kalksın

        if (durumkurumadi || durummail || durumpassword
            || !mail.getText().toString().contains("@")) { //içermediği durum

          if (durumkurumadi)
            tilkurumadi.setError("Lütfen ad ve soyad giriniz.");
          else if (durummail)
            tilmail.setError("Lütfen mail adresinizi giriniz.");
          else if (durumpassword)
            tilpassword.setError("Lütfen bir şifre giriniz.");
          else if (!mail.getText().toString().contains("@"))
            tilmail.setError("Lütfen geçerli bir mail giriniz.");
        } else {
          mAuth.createUserWithEmailAndPassword(mail.getText().toString(), password.getText().toString())
              .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                  // check user data already exits
                  UserData userData = new UserData();
                  userData.userType = UserType.COMMERCIAL;
                  userData.fullName = kurumadi.getText().toString();
                  userData.score = 5;
                  userData.mail = mail.getText().toString();

                  ref.child(mAuth.getUid()).setValue(userData);
                  startActivity(new Intent(RegisterActivity2.this, MapsActivity2.class));
                }
              })
              .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                  Toast.makeText(RegisterActivity2.this, "Kayıt hatası.", Toast.LENGTH_SHORT).show();
                }
              });
        }
        break;

    }
  }
}
