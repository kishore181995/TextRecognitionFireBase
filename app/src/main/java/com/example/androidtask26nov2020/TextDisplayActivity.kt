package com.example.androidtask26nov2020

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.example.androidtask26nov2020.R

class TextDisplayActivity: AppCompatActivity() {

    lateinit var text : AppCompatTextView
    lateinit var imageDisplay: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tezt_display)

        val ss: String? = intent.getStringExtra("text")
        val bp : String? = intent.getStringExtra("pic")
        Log.d("BP",""+bp);
        val uri = Uri.parse(bp)
        imageDisplay = findViewById(R.id.imageDisplayView)
        //setting image from mainactivity screen to display in imageview as per functionality
        imageDisplay.setImageURI(uri)

        text = findViewById(R.id.text)
        //setting text from mainactivity screen to display in test in line -by -line  as per functionality
        text.setText(""+ss)

    }

}