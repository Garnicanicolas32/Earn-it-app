package com.example.rewardskotlin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rewardskotlin.dataAndClasses.Reward
import com.example.rewardskotlin.dataAndClasses.SaveFormat
import com.example.rewardskotlin.databinding.ActivityConfigBinding
import com.google.gson.Gson
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

private const val MAINACTIVITYKEY = "configtomain2"
private const val SHARED = "Shared8"
private const val KEY = "MainKey8"

class Config : AppCompatActivity() {
    private lateinit var viewBinding: ActivityConfigBinding
    private val listEmpty: List<Reward> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityConfigBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.bottomBar.menu.getItem(4).isChecked = true

        viewBinding.btnFirsttime.setOnClickListener {
            val intent = Intent(this, FirstTimeActivity::class.java)
            startActivity(intent)
        }

        //first blank
        var saveFormat = Gson().toJson(SaveFormat(
            listEmpty,
            listEmpty,
            0,
            1.2f,
            true
        ))

        //check if exist
        val str: String? = this.getSharedPreferences(SHARED, 0).getString(KEY, "hello")//saveFormat)


        viewBinding.txtOutput.text = str

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

    @Throws(IOException::class)
    fun compress(string: String): ByteArray? {
        val os = ByteArrayOutputStream(string.length)
        val gos = GZIPOutputStream(os)
        gos.write(string.toByteArray())
        gos.close()
        val compressed: ByteArray = os.toByteArray()
        os.close()
        return compressed
    }

    @Throws(IOException::class)
    fun decompress(compressed: ByteArray?): String? {
        val BUFFER_SIZE = 32
        val `is` = ByteArrayInputStream(compressed)
        val gis = GZIPInputStream(`is`, BUFFER_SIZE)
        val string = StringBuilder()
        val data = ByteArray(BUFFER_SIZE)
        var bytesRead: Int
        while (gis.read(data).also { bytesRead = it } != -1) {
            string.append(String(data, 0, bytesRead))
        }
        gis.close()
        `is`.close()
        return string.toString()
    }
}