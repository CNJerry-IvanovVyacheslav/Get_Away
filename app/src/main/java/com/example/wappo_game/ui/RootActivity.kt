package com.example.wappo_game.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.wappo_game.R

class RootActivity : AppCompatActivity(R.layout.main_menu) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("test")
    }
}