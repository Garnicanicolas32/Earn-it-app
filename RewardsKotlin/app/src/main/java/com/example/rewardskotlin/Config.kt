package com.example.rewardskotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.rewardskotlin.databinding.ActivityConfigBinding

class Config : AppCompatActivity() {

    private lateinit var viewBinding: ActivityConfigBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityConfigBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)


    }
}