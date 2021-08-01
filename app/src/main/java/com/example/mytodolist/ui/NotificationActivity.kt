package com.example.mytodolist.ui

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.mytodolist.R


class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        val text = intent.getStringExtra(EXTRA_TEXT) ?: ""

    }

    companion object {
        const val EXTRA_TEXT = "extra_text"
    }

}