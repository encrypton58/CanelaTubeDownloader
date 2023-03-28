package com.m_and_a_company.canelatube

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class InitActivitySp : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val screenSplash = installSplashScreen()
        setContentView(R.layout.activity_splash)
        //TODO: Agregar logica necesaria
        screenSplash.setKeepOnScreenCondition {
            true
        }
        startActivity(Intent(applicationContext, MainActivity::class.java))
        finish()
    }
}