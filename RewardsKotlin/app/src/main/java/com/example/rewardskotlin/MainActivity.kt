package com.example.rewardskotlin

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rewardskotlin.dataAndClasses.MutablePoints
import com.example.rewardskotlin.dataAndClasses.SaveFormat
import com.example.rewardskotlin.dataAndClasses.Reward
import com.example.rewardskotlin.adapter.rewardAdapter
import com.example.rewardskotlin.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    //KEYS
    private val SHARED = "HelloItsamemario"
    private val KEY = "NewKEY"
    private val FIRSTIME = "JesusChrist"

    //GLOBAL VAR
    private val listEmpty: List<Reward> = listOf<Reward>()
    private var globalData = SaveFormat(
        listEmpty,
        listEmpty,
        0,
        1f
    )

    //LATEINIT VARS
    private lateinit var preferences: SharedPreferences
    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var mutablePoints: MutablePoints

    //ON CREATE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //INFLATE BINDING
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        //DATA MANAGMENT
        preferences = this.getSharedPreferences(SHARED, 0)
        if (preferences.getBoolean(FIRSTIME, true)) {
            //first time (STILL DOESN'T WORK)
            preferences.edit().putBoolean(FIRSTIME, false).apply()
        }
        globalData = loadData()

        //MUTABLE POINTS
        mutablePoints = ViewModelProvider(this).get(MutablePoints::class.java)

        viewBinding.txtPoints.text = globalData.globalPoints.toString()
        mutablePoints.changePoints(globalData.globalPoints)

        mutablePoints.currentPoints.observe(this) {
            viewBinding.txtPoints.text = it.toString()
        }

        //RECYCLER VIEW
        initRecyclerView(globalData.listActivities)

        viewBinding.btnListActivities.setOnClickListener {
            initRecyclerView(globalData.listActivities)
        }
        viewBinding.btnListRewards.setOnClickListener {
            initRecyclerView(globalData.listRewards)
        }

        //CREATE REWARD
        viewBinding.btnCreateReward.setOnClickListener {
            saveData(globalData)
            val intent = Intent(this, CreateReward::class.java)
            startActivity(intent)
        }
        //-- if new reward obtained
        if (!intent.getStringExtra("NewReward").isNullOrBlank()) {
            val newRewardObtained =
                Gson().fromJson(intent.getStringExtra("NewReward") + "", Reward::class.java)
            globalData.listRewards = globalData.listRewards + newRewardObtained
            saveData(globalData)
            refresh()
        }
    }

    //Recycler View
    private fun initRecyclerView(lista: List<Reward>) {
        viewBinding.listOfRewards.layoutManager = LinearLayoutManager(this)
        viewBinding.listOfRewards.adapter = rewardAdapter(lista) {
            onItemSelected(
                it
            )
        }
    }

    private fun onItemSelected(objectiveReward: Reward) {
        // -V- apreto boton -V-
        if (objectiveReward.isModify)
            Toast.makeText(this, "Modificar " + objectiveReward.name, Toast.LENGTH_SHORT).show()
        else if (!mutablePoints.changePoints(objectiveReward.price))
            Toast.makeText(this, "Puntos Insuficientes", Toast.LENGTH_SHORT).show()
    }

    //DATA FUNCTIONS
    private fun refresh() {
        globalData.listRewards.forEach{
           it.price = ((it.basePrice * it.discountMOD * it.usagePercentageCount * if(it.isReward) globalData.rewardRatio else 1f ) / 5).toInt() * 5
        }
        Toast.makeText(this,"amogus",Toast.LENGTH_SHORT).show()

        initRecyclerView(globalData.listRewards)
    }

    private fun saveData(saveFormat: SaveFormat) {
        preferences.edit().clear().apply()
        preferences.edit().putString(KEY, Gson().toJson(saveFormat)).apply()
    }

    private fun loadData(): SaveFormat {

        //first blank
        var saveFormat = SaveFormat(
            listEmpty,
            listEmpty,
            0,
            1f
        )
        //check if exist

        val str: String? = preferences.getString(KEY, "")
        if (!str.isNullOrBlank())
            saveFormat = Gson().fromJson(str, SaveFormat::class.java)

        return saveFormat
    }
}