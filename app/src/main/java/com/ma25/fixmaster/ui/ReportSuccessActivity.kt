package com.ma25.fixmaster.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.ma25.fixmaster.R
import com.ma25.fixmaster.UserDashboardActivity

class ReportSuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_success)

        // Timer för automatisk navigering efter 3 sekunder
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, UserDashboardActivity::class.java)
            // Rensar historiken så användaren inte kan gå "bakåt" till tacksidan
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }, 3000)
    }
}