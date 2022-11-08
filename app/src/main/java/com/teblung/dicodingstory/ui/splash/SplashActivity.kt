package com.teblung.dicodingstory.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.teblung.dicodingstory.R
import com.teblung.dicodingstory.data.source.local.preference.SessionUser
import com.teblung.dicodingstory.databinding.ActivitySplashBinding
import com.teblung.dicodingstory.ui.home.MainActivity
import com.teblung.dicodingstory.ui.login.LoginActivity

class SplashActivity : AppCompatActivity() {

    private val binding: ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }
    private val twoSecond: Long = 4000

    private lateinit var preferences: SessionUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupUI()
    }

    private fun setupUI() {
        binding.run {
            val animUp = AnimationUtils.loadAnimation(this@SplashActivity, R.anim.slide_up)
            imgSplash.startAnimation(animUp)
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    checkSession()
                }, twoSecond
            )
        }
    }

    private fun checkSession() {
        preferences = SessionUser(this)
        if (preferences.getLoginData().isLogin) {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        }
    }
}