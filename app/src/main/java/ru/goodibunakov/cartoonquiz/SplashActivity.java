package ru.goodibunakov.cartoonquiz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        ImageView rays = findViewById(R.id.rays);
        Animation animationRotateCenter = AnimationUtils.loadAnimation(
                this, R.anim.rotate_center);
        rays.startAnimation(animationRotateCenter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        final View decorView = getWindow().getDecorView();
        hideSystemUI(decorView);
    }

    // This snippet hides the system bars.
    private void hideSystemUI(View decorView) {

        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        Log.i("HideSystemUI work", "cool");
    }


}
