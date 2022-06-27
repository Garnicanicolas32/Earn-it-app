package com.example.rewardskotlin

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rewardskotlin.dataAndClasses.Reward
import com.example.rewardskotlin.dataAndClasses.SaveFormat
import com.example.rewardskotlin.databinding.ActivityConfigBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder


private const val MAINACTIVITYKEY = "configtomain2"
private const val SHARED = "Shared8"
private const val KEY = "MainKey8"

class Config : AppCompatActivity() {
    private lateinit var viewBinding: ActivityConfigBinding
    private val listEmpty: List<Reward> = listOf()
    private var left = 3
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityConfigBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.bottomBar.menu.getItem(4).isChecked = true

        viewBinding.btnFirsttime.setOnClickListener {
            val intent = Intent(this, FirstTimeActivity::class.java)
            startActivity(intent)
        }

        val preferences = this.getSharedPreferences(SHARED, 0)
        //first blank
        var saveFormat = Gson().toJson(SaveFormat(
            listEmpty,
            listEmpty,
            0,
            1.2f,
            true
        ))

        //check if exist
        val str: String? = preferences.getString(KEY, saveFormat)//saveFormat)

        viewBinding.txtOutput.text = str

        viewBinding.button3.setOnClickListener {
            try {
                val o: SaveFormat = Gson().fromJson(viewBinding.txtInput.text.toString(), SaveFormat::class.java)
                if(left > 0){
                    Toast.makeText(this, "Press $left more times to import data", Toast.LENGTH_SHORT).show()
                    left --
                }else{
                    preferences.edit().clear().apply()
                    preferences.edit().putString(KEY, Gson().toJson(o)).apply()
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show()
            }
        }

        viewBinding.button.setOnClickListener {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", str)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(this, "Text Copied!", Toast.LENGTH_SHORT).show()
        }

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
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }
}