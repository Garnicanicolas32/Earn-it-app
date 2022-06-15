package com.example.rewardskotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.rewardskotlin.databinding.ActivityFirstTimeBinding

class First_time_activity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityFirstTimeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityFirstTimeBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)


    }
}