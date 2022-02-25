package ru.goodibunakov.cartoonquiz

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.goodibunakov.cartoonquiz.databinding.ActivityMainBinding
import java.util.*

class MainActivity: AppCompatActivity(R.layout.activity_main) {

    private val binding by viewBinding(ActivityMainBinding::bind, R.id.container)

    var mp: MediaPlayer? = null
    var res: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val animationRotateCenter = AnimationUtils.loadAnimation(
            this, R.anim.rotate_center
        )
        binding.mainRays.startAnimation(animationRotateCenter)
        res = Random().nextInt(7)
    }

    override fun onStart() {
        super.onStart()
//        MainActivityFragment quizFragment = (MainActivityFragment)
//                getSupportFragmentManager().findFragmentById(
//                        R.id.quizFragment);
    }

    override fun onResume() {
        super.onResume()
        val decorView = window.decorView
        hideSystemUI(decorView)

        //установка случайной мелодии
//        if (mp == null) {
//            mp = when (res) {
//                0 -> MediaPlayer.create(this, R.raw.chillgameloop)
//                1 -> MediaPlayer.create(this, R.raw.coolgamethemeloop)
//                2 -> MediaPlayer.create(this, R.raw.dailyspecialshort)
//                3 -> MediaPlayer.create(this, R.raw.dolphinrideshort)
//                4 -> MediaPlayer.create(this, R.raw.followmeshort)
//                5 -> MediaPlayer.create(this, R.raw.petparkshort)
//                else -> MediaPlayer.create(this, R.raw.pinballthreeshort)
//            }
//            mp.setAudioStreamType(AudioManager.STREAM_MUSIC)
//            mp.setLooping(true)
//            mp.start()
//        }
    }

    // This snippet hides the system bars.
    private fun hideSystemUI(decorView: View) {

        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        } else {
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN) // hide status bar
        }
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onStop() {
        super.onStop()
//        if (mp != null) {
//            mp.stop()
//            mp.release()
//            mp = null
//        }
    }

    override fun onBackPressed() {}
}