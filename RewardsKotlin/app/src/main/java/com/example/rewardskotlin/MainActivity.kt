package com.example.rewardskotlin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rewardskotlin.adapter.rewardAdapter
import com.example.rewardskotlin.dataAndClasses.MutablePoints
import com.example.rewardskotlin.dataAndClasses.MyOwnClock
import com.example.rewardskotlin.dataAndClasses.Reward
import com.example.rewardskotlin.dataAndClasses.SaveFormat
import com.example.rewardskotlin.databinding.ActivityMainBinding
import com.google.gson.Gson
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

class MainActivity : AppCompatActivity() {
    //Podes crear, modificar y eleminar recompensas y actividades
    //Esta programado que cambie el precio segun Uso, cuantas veces al mes se usa la recompensa y prioridad
    //Se guarda tod0 en el celu asi que si cerras la app cuando abras devuelta sigue estando
    //

    ///TO-DO:
    ///      Sort by points or Alphabetically
    ///      import export data
    ///        CHECK TIME
    ///      ADD DISCOUNTS
    ///      Implement time (remove discount and usage)
    ///      IMPLEMENT TAGS
    ///      IMPLEMENT CONFIGURATION
    /// LIMITED TIMES (implementado solo falta AVISAR)

    //KEYS
    private val SHARED = "Shared4"
    private val KEY = "MainKey4"
    private val FIRSTIME = "IsFirstTime4"

    //GLOBAL VAR
    private var howManyDaysToDiscount = 2
    private var rewardSelected = false
    private val listEmpty: List<Reward> = listOf()
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

    //COLORS
    private val NOTSELECTEDCOLOR = "#ccbb8b"
    private val SELECTEDCOLOR = "#fcba03"

    ///// ON CREATE /////
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
            globalData.globalPoints = it
        }

        //RECYCLER VIEW
        initRecyclerView(globalData.listActivities)

        viewBinding.btnListActivities.setOnClickListener {
            initRecyclerView(globalData.listActivities)
            rewardSelected = false
            viewBinding.btnListActivities.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
            viewBinding.btnListRewards.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
        }
        viewBinding.btnListRewards.setOnClickListener {
            initRecyclerView(globalData.listRewards)
            rewardSelected = true
            viewBinding.btnListActivities.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
            viewBinding.btnListRewards.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
        }

        //CREATE REWARD
        viewBinding.btnCreateReward.setOnClickListener {
            createRewardGoview()
        }
        //-- if new reward obtained
        if (!intent.getStringExtra("NewReward").isNullOrBlank()) {
            val newRewardObtained =
                Gson().fromJson(intent.getStringExtra("NewReward") + "", Reward::class.java)
            if (newRewardObtained.isDelete) {
                deleteReward(newRewardObtained)
            } else {
                if (intent.getBooleanExtra("isChangeAndDelete", false)) {
                    val oldReward =
                        Gson().fromJson(intent.getStringExtra("OldObject") + "", Reward::class.java)
                    deleteReward(oldReward)
                }
                if (newRewardObtained.isReward) {
                    globalData.listRewards = globalData.listRewards + newRewardObtained
                    rewardSelected = true
                } else {
                    globalData.listActivities = globalData.listActivities + newRewardObtained
                    rewardSelected = false
                }
            }
            saveData(globalData)
            refresh()
        }

        viewBinding.swiperefresh.setOnRefreshListener {
            refresh()
            viewBinding.swiperefresh.isRefreshing = false
        }

        //----------------TEMPORAL DELETE ALL // REMOVE IN FINAL
        viewBinding.btnCheckList.setOnClickListener {
           Log.i("Rewards", Gson().toJson(globalData.listRewards))
           Log.i("Activities", Gson().toJson(globalData.listActivities))
        }
        //----------------TEMPORAL DELETE ALL // REMOVE IN FINAL
        viewBinding.btnDeleteAll.setOnClickListener {
            globalData.listRewards = emptyList()
            globalData.listActivities = emptyList()
            saveData(globalData)
            refresh()
        }
        //----------------TEMPORAL REMOVE USAGE // REMOVE IN FINAL
        viewBinding.btnRemoveUsage.setOnClickListener {
            globalData.listRewards.forEach {
                it.usagePercentageCount = 1f
            }
            globalData.listActivities.forEach {
                it.usagePercentageCount = 1f
            }
            refresh()
        }

        refresh() //LASTLY
        //startTimeCounter()
    }

    //TIME MANAGMENT

    //Recycler View
    private fun initRecyclerView(lista: List<Reward>) {
        viewBinding.listOfRewards.layoutManager = LinearLayoutManager(this)
        viewBinding.listOfRewards.adapter = rewardAdapter(lista) {
            onItemSelected(
                it
            )
        }
    }

    private fun onItemSelected(clickedReward: Reward) {
        //modify
        val index = getIndexSavedReward(clickedReward)
        if (clickedReward.isModify) {
            createRewardGoview(clickedReward)
        } else {
            Log.i("Price", clickedReward.price.toString())
            if (mutablePoints.changePoints(clickedReward.price)) {//Enough points

                if (clickedReward.isReward) clickedReward.usagePercentageCount += clickedReward.usagePercentageADD
                clickedReward.lastTimeUsed = MyOwnClock(LocalDateTime.now())
                //clickedReward.lastTimeChecked = MyOwnClock(LocalDateTime.now())
                if (index >= 0)
                    modifyReward(index, clickedReward)
                else
                    Toast.makeText(this, "item not found", Toast.LENGTH_SHORT).show()

                if (clickedReward.isLimited) if (minusOneLimited(clickedReward)) { //IF bought and limited
                    deleteReward(clickedReward)
                }//This has to be the last thing because it deletes the reward
            } else //Not enough points
                Toast.makeText(this, "Puntos Insuficientes", Toast.LENGTH_SHORT).show() //TEMP??
        }
        refresh()
    }

    //MODIFY EDIT INDEX EVERYTHING TO DO WITH REWARD
    private fun modifyReward(index: Int, theNewOne: Reward) {
        if (theNewOne.isReward) {
            val bufferListRewards = globalData.listRewards.toMutableList()
            bufferListRewards[index] = theNewOne
            globalData.listRewards = bufferListRewards
        } else {
            val bufferListRewards = globalData.listActivities.toMutableList()
            bufferListRewards[index] = theNewOne
            globalData.listActivities = bufferListRewards
        }
    }

    private fun deleteReward(theONE: Reward) {
        val index = getIndexSavedReward(theONE)
        if (index >= 0) {
            if (theONE.isReward) {
                val bufferListRewards = globalData.listRewards.toMutableList()
                bufferListRewards.removeAt(index)
                globalData.listRewards = bufferListRewards
            } else {
                val bufferListRewards = globalData.listActivities.toMutableList()
                bufferListRewards.removeAt(index)
                globalData.listActivities = bufferListRewards
            }
        }
    }

    private fun minusOneLimited(reward: Reward): Boolean {
        var noMore = false
        val list = if (reward.isReward) {
            globalData.listRewards
        } else globalData.listActivities
        val index = getIndexSavedReward(reward)
        if (index >= 0) {
            if (list[index].limitedTimes <= 1) noMore = true
            else list[index].limitedTimes--

            if (reward.isReward) globalData.listRewards = list
            else globalData.listActivities = list
        }
        return noMore
    }

    private fun getIndexSavedReward(theONE: Reward): Int {
        var founded = -1
        if (theONE.isReward) {
            globalData.listRewards.forEachIndexed { index, element ->
                if (theONE.name == element.name && theONE.basePrice == element.basePrice) {
                    founded = index
                }
            }
        } else {
            globalData.listActivities.forEachIndexed { index, element ->
                //if (theONE == element) {
                if (theONE.name == element.name && theONE.basePrice == element.basePrice) {
                    founded = index
                }
            }
        }
        return founded
    }

    //CREATE REWARD - CHANGE VIEW
    private fun createRewardGoview(reward: Reward) {
        saveData(globalData)
        val intent = Intent(this, CreateReward::class.java)
        intent.putExtra("ModifyRewardKEY", Gson().toJson(reward))
        startActivity(intent)
    }

    private fun createRewardGoview() {
        saveData(globalData)
        val intent = Intent(this, CreateReward::class.java)
        startActivity(intent)
    }

    //DATA FUNCTIONS
    private fun refresh() {
        //Los 2 son mismo, si cambia uno, cambiar el otro (Ver desp como hacer que "IT" sea Var instead of Val)
        globalData.listRewards.forEach {

            val today = LocalDateTime.now()

            if(it.discountRemoveAfter.devolver().isAfter(today)){//Discount Vencio
                it.discountMOD = 1f
                it.discountRemoveAfter = MyOwnClock(LocalDateTime.MAX)
                }

            val dayPassed = today.isAfter(it.lastTimeUsed.devolver().plusDays(1))
            val hourPassed = today.isAfter(it.lastTimeUsed.devolver().plusHours(1))
            val duration = Duration.between(it.lastTimeUsed.devolver(), today)

            if(hourPassed && it.usagePercentageCount != 1f && !dayPassed){
                //Hours but not days
                val removeTimes = duration.toHours().toInt() - it.timesRemoved //How many more hours to remove
                it.timesRemoved = duration.toHours().toInt()
                val new = it.usagePercentageCount - (-it.usagePercentageADD * removeTimes)
                it.usagePercentageCount = if(new > 1f) new else 1f

            }else if(dayPassed){
                //Days passed
                if(duration.toDays().toInt() > howManyDaysToDiscount){
                    it.discountMOD = 1.25f //temp
                    it.discountRemoveAfter = MyOwnClock(today.plusDays(1))
                }
                it.usagePercentageCount = 1f
            }

            it.price =
                ((it.basePrice * it.discountMOD * it.usagePercentageCount * globalData.rewardRatio) / 5).toInt() * 5
        }
        globalData.listActivities.forEach {
            val today = LocalDateTime.now()

            if(it.discountRemoveAfter.devolver().isAfter(today)){//Discount Vencio
                it.discountMOD = 1f
                it.discountRemoveAfter = MyOwnClock(LocalDateTime.MAX)
            }

            val dayPassed = today.isAfter(it.lastTimeUsed.devolver().plusDays(1))
            val hourPassed = today.isAfter(it.lastTimeUsed.devolver().plusHours(1))
            val duration = Duration.between(it.lastTimeUsed.devolver(), today)

            if(hourPassed && it.usagePercentageCount != 1f && !dayPassed){
                //Hours but not days
                val removeTimes = duration.toHours().toInt() - it.timesRemoved //How many more hours to remove
                it.timesRemoved = duration.toHours().toInt()
                val new = it.usagePercentageCount - (-it.usagePercentageADD * removeTimes)
                it.usagePercentageCount = if(new > 1f) new else 1f

            }else if(dayPassed){
                //Days passed
                if(duration.toDays().toInt() > howManyDaysToDiscount){
                    it.discountMOD = 1.25f //temp
                    it.discountRemoveAfter = MyOwnClock(today.plusDays(1))
                }
                it.usagePercentageCount = 1f
            }

            it.price =
                ((it.basePrice * it.discountMOD * it.usagePercentageCount) / 5).toInt() * 5
        }

        if (rewardSelected){
            viewBinding.btnListActivities.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
            viewBinding.btnListRewards.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
            initRecyclerView(globalData.listRewards)}
        else {
            viewBinding.btnListActivities.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
            viewBinding.btnListRewards.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
            initRecyclerView(globalData.listActivities)
        }
        saveData(globalData)
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

    fun startTimeCounter() {
        object : CountDownTimer(60000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                refresh()
                startTimeCounter()
            }
        }.start()
    }
}