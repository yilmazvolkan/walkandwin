package com.boun.volkanyilmaz.walkandwin;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class ChoseActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Fabric.with(this, new Crashlytics());
    animations();
  }

  public void animations() {
    setContentView(R.layout.activity_chose);
    ImageView image = findViewById(R.id.imageChose);
    Window window = getWindow();
    window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    WindowManager wm = window.getWindowManager();
    Display screen = wm.getDefaultDisplay();
    Point point = new Point();
    screen.getSize(point);
    int genis = point.x;
    int yuksek = point.y;

    image.getLayoutParams().width = (int) (yuksek * 2.23);
    image.getLayoutParams().height = yuksek;

    ObjectAnimator animator = ObjectAnimator.ofFloat(image, "x", 0, -(yuksek * 2.23f - genis), 0, -(yuksek * 2.23f - genis));
    animator.setDuration(210000);
    animator.setInterpolator(new LinearInterpolator());
    animator.start();
  }

  public void click(View v) {
    switch (v.getId()) {
      case R.id.individual: //Bireysel
        startActivity(new Intent(ChoseActivity.this, LoginActivity.class));
        break;
      case R.id.commercial: //Kurumsal
        startActivity(new Intent(ChoseActivity.this, LoginActivity2.class));
        break;
    }
  }
}
