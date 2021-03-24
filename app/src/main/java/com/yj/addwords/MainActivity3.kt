package com.yj.addwords

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast

class MainActivity3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        var a = findViewById<ImageView>(R.id.application_setting)
        a.setOnClickListener { Toast.makeText(this, "ㅇㅇ", Toast.LENGTH_SHORT).show()}
    }
}