package com.samyak2403.arrowchatapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {

    // Duration for the splash screen to be displayed (in milliseconds)
    private val splashTimer: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        // Set the activity to full screen
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // Use Handler to delay the execution of code
        Handler().postDelayed({
            // Start the MainActivity after the splash screen timer
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Finish the SplashScreen activity
        }, splashTimer)
    }
}