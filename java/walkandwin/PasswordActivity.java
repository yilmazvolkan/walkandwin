package com.boun.volkanyilmaz.walkandwin;

import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.os.Bundle;
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

import com.google.firebase.auth.FirebaseAuth;

public class PasswordActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextInputLayout tilmail;
    private TextInputEditText mail;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        animations();
        androidTools();
    }
    public void androidTools(){
        tilmail = findViewById(R.id.tilmailadresisifre);

        mail = findViewById(R.id.mailadresisifre);
    }
    public void animations(){
        setContentView(R.layout.activity_password);
        ImageView image = findViewById(R.id.imagePassword);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager wm = window.getWindowManager();
        Display screen = wm.getDefaultDisplay();
        Point point = new Point();
        screen.getSize(point);

        int genis = point.x;
        int yuksek = point.y;

        image.getLayoutParams().width = (int) (yuksek* 2.22);
        image.getLayoutParams().height = yuksek;

        ObjectAnimator animator = ObjectAnimator.ofFloat(image, "x" , 0 , -(yuksek*2.22f-genis),0,-(yuksek*2.22f-genis));
        animator.setDuration(210000);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    public void click(View v) {
        switch (v.getId()) {
            case R.id.sifremiGetir:
                boolean durummail= TextUtils.isEmpty(mail.getText());
                tilmail.setError(null);
                if(durummail || !mail.getText().toString().contains("@")){ //içermediği durum
                    if(durummail)
                        tilmail.setError("Lütfen mail adresinizi giriniz.");
                    else if(!mail.getText().toString().contains("@"))
                        tilmail.setError("Lütfen geçerli bir mail giriniz.");
                }
                else {
                    mAuth.sendPasswordResetEmail(mail.getText().toString());
                    Toast.makeText(PasswordActivity.this, "Şifrenizi değiştirmek için e-posta gönderdik.", Toast.LENGTH_SHORT).show();
                }
        }
    }
}
