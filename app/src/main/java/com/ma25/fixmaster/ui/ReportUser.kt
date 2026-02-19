package com.ma25.fixmaster

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ReportUser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Viktigt: Se till att detta namn matchar din XML-fils namn exakt
        setContentView(R.layout.user_report)
    }
}