package ru.goodibunakov.cartoonquiz

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.goodibunakov.cartoonquiz.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity(R.layout.activity_splash) {

    private val binding by viewBinding(ActivitySplashBinding::bind, R.id.container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val animationRotateCenter = AnimationUtils.loadAnimation(
            this, R.anim.rotate_center
        )
        binding.rays.startAnimation(animationRotateCenter)
        val animationBounce1 = AnimationUtils.loadAnimation(
            this, R.anim.bounce1
        )
        binding.logoLevel1.startAnimation(animationBounce1)
        val animationBounce2 = AnimationUtils.loadAnimation(
            this, R.anim.bounce2
        )
        val animationBounce3 = AnimationUtils.loadAnimation(
            this, R.anim.bounce3
        )
        binding.logoLevel2.startAnimation(animationBounce2)
        binding.logoLevel3.startAnimation(animationBounce2)
        binding.logoLevel4.startAnimation(animationBounce3)
        binding.logoLevel5.startAnimation(animationBounce2)
        binding.ivBtnStart.setOnClickListener { view: View? ->
            val start = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(start)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    override fun onResume() {
        super.onResume()
        val decorView = window.decorView
        hideSystemUI(decorView)
    }

    // This snippet hides the system bars.
    private fun hideSystemUI(decorView: View) {

        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
        } else {
            decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                )
        }
    }
}