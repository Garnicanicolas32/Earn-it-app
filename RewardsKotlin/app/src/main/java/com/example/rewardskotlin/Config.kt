package com.example.rewardskotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.rewardskotlin.databinding.ActivityConfigBinding

private const val MAINACTIVITYKEY = "configtomain2"

class Config : AppCompatActivity() {
    private lateinit var viewBinding: ActivityConfigBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityConfigBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.bottomBar.menu.getItem(4).isChecked = true

        viewBinding.bottomBar.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.BottomList -> {
                    changeAct(0)
                    true
                }
                R.id.BottomAdd -> {
                    changeAct(4)
                    true
                }
                R.id.BottomEdit -> {
                    changeAct(2)
                    true
                }
                R.id.BottomDelete -> {
                    changeAct(1)
                    true
                }
                else -> false
            }
        }
    }
    private fun changeAct(option: Int){
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MAINACTIVITYKEY, option)
        startActivity(intent)
    }
}