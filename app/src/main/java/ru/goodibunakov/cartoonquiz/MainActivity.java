package ru.goodibunakov.cartoonquiz;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_rays)
    ImageView rays;

    MediaPlayer mp;
    Random random;
    int res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Animation animationRotateCenter = AnimationUtils.loadAnimation(
                this, R.anim.rotate_center);
        rays.startAnimation(animationRotateCenter);

        random = new Random();
        res = random.nextInt(7);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        MainActivityFragment quizFragment = (MainActivityFragment)
//                getSupportFragmentManager().findFragmentById(
//                        R.id.quizFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final View decorView = getWindow().getDecorView();
        hideSystemUI(decorView);

        if (mp == null) {
            switch (res) {
                case 0:
                    mp = MediaPlayer.create(this, R.raw.chillgameloop);
                    break;
                case 1:
                    mp = MediaPlayer.create(this, R.raw.coolgamethemeloop);
                    break;
                case 2:
                    mp = MediaPlayer.create(this, R.raw.dailyspecialshort);
                    break;
                case 3:
                    mp = MediaPlayer.create(this, R.raw.dolphinrideshort);
                    break;
                case 4:
                    mp = MediaPlayer.create(this, R.raw.followmeshort);
                    break;
                case 5:
                    mp = MediaPlayer.create(this, R.raw.petparkshort);
                    break;
                default:
                    mp = MediaPlayer.create(this, R.raw.pinballthreeshort);
                    break;
            }
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setLooping(true);
            mp.start();
        }
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

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    @Override
    public void onBackPressed() {
    }
}
