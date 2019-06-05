package ru.goodibunakov.cartoonquiz;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.logo_level1)
    ImageView logo1;
    @BindView(R.id.logo_level2)
    ImageView logo2;
    @BindView(R.id.logo_level3)
    ImageView logo3;
    @BindView(R.id.logo_level4)
    ImageView logo4;
    @BindView(R.id.logo_level5)
    ImageView logo5;
    @BindView(R.id.iv_btn_start)
    ImageView btnStart;
    @BindView(R.id.rays)
    ImageView rays;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        Animation animationRotateCenter = AnimationUtils.loadAnimation(
                this, R.anim.rotate_center);
        rays.startAnimation(animationRotateCenter);

        Animation animationBounce1 = AnimationUtils.loadAnimation(
                this, R.anim.bounce1);
        logo1.startAnimation(animationBounce1);
        Animation animationBounce2 = AnimationUtils.loadAnimation(
                this, R.anim.bounce2);
        Animation animationBounce3 = AnimationUtils.loadAnimation(
                this, R.anim.bounce3);

        logo2.startAnimation(animationBounce2);
        logo3.startAnimation(animationBounce2);
        logo4.startAnimation(animationBounce3);
        logo5.startAnimation(animationBounce2);

        btnStart.setOnClickListener(view -> {
            Intent start = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(start);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN); // hide status bar
        }
    }
}