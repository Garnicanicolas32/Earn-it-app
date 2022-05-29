package com.example.rewardskotlin

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rewardskotlin.adapter.rewardAdapter
import com.example.rewardskotlin.dataAndClasses.*
import com.example.rewardskotlin.databinding.ActivityMainBinding
import com.google.gson.Gson
import java.time.Duration
import java.time.LocalDateTime

///TO-DO:
///      Sort by points or Alphabetically
///      import export data
///      ADD DISCOUNTS
///      IMPLEMENT TAGS
///      IMPLEMENT CONFIGURATION
/// LIMITED TIMES (implementado solo falta AVISAR)

//FIXED VALUES YOU CAN EDIT
private var USAGEPORCENTAGEADD = 0.25f
private var howManyDaysToDiscount = 2

//--colors
private const val NOTSELECTEDCOLOR = "#ccbb8b"
private const val SELECTEDCOLOR = "#fcba03"

//KEYS
private const val DEFAULTTAG = "default"
private const val KEYsendAndGo = "recieve7"
private const val KEYpackage = "package7"
private const val SHARED = "Shared7"
private const val KEY = "MainKey7"
private const val FIRSTIME = "IsFirstTime7"

private val LETTERS = listOf('a','b','c','d','e','f','g','h','i','j','k')
private val LETTERSREVERSE = LETTERS.reversed()

class MainActivity : AppCompatActivity() {
    //GLOBAL VAR
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
            createRewardGoview(null)
        }
        //-- if new reward obtained
        val jsonObtained: String? = intent.getStringExtra(KEYsendAndGo)
        if (!jsonObtained.isNullOrBlank()) {
            val obtained =
                Gson().fromJson(jsonObtained, SendBack::class.java)
            if (obtained.isDelete) {//if its deleting an existing one
                rewardSelected = obtained.reward.basePrice < 0
                deleteReward(obtained.reward)
            } else {//new or editing an old one
                if (obtained.isEdit) {
                    deleteReward(obtained.oldOne!!)
                    rewardSelected = obtained.reward.basePrice < 0
                }
                if (obtained.reward.basePrice < 0) {
                    globalData.listRewards = globalData.listRewards + obtained.reward
                    rewardSelected = true
                } else {
                    globalData.listActivities = globalData.listActivities + obtained.reward
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

        viewBinding.spinnerSort.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.ordenar)
        )
        viewBinding.spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                Log.i("Paso","entra")
                var listaR = globalData.listRewards
                var listaA = globalData.listActivities
                when(position){
                    0 -> {
                        Log.i("Paso"," alpha")

                        listaR = sortAlphabetically(globalData.listRewards)
                        listaA = sortAlphabetically(globalData.listActivities)
                    }
                    1 -> {
                        Log.i("Paso","puntos")

                        listaR = sortByPoints(globalData.listRewards, LETTERS)
                        listaA = sortByPoints(globalData.listActivities, LETTERS)
                    }
                    2 -> {
                        Log.i("Paso","puntos")

                        listaR = sortByPoints(globalData.listRewards, LETTERSREVERSE)
                        listaA = sortByPoints(globalData.listActivities, LETTERSREVERSE)
                    }
                }
                globalData.listActivities = listaA
                globalData.listRewards = listaR
                refresh()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // another interface callback
            }
        }

        //----------------TEMPORAL DO ANYTHING // REMOVE IN FINAL
        viewBinding.btnDebugAnything.setOnClickListener {

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

    ////FUNCTIONS////

    //Recycler View
    private fun initRecyclerView(lista: List<Reward>) {
        viewBinding.listOfRewards.layoutManager = LinearLayoutManager(this)
        viewBinding.listOfRewards.adapter = rewardAdapter(lista) { reward ->
            onItemSelected(
                reward
            )
        }
    }

    private fun onItemSelected(clickedReward: OnClickReturn) {
        //modify
        if (clickedReward.isEdit) {
            createRewardGoview(clickedReward.reward)
        } else {
            if (mutablePoints.changePoints(clickedReward.reward.price)) {//Enough points
                //modify
                if (clickedReward.reward.basePrice < 0)
                    clickedReward.reward.usagePercentageCount += USAGEPORCENTAGEADD
                clickedReward.reward.lastTimeUsed = MyOwnClock(LocalDateTime.now())
                //save
                val index = getIndexSavedReward(clickedReward.reward)
                if (index >= 0)
                    modifyReward(index, clickedReward.reward)

                if (clickedReward.reward.limitedTimes > -1) if (minusOneLimited(clickedReward.reward)) { //IF bought and limited
                    deleteReward(clickedReward.reward)
                }//This has to be the last thing because it deletes the reward
            } // else //Not enough points
        }
        refresh()
    }

    //MODIFY EDIT INDEX EVERYTHING TO DO WITH REWARD
    private fun modifyReward(index: Int, theNewOne: Reward) {
        if (theNewOne.basePrice < 0) {
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
            if (theONE.basePrice < 0) {
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
        val list = if (reward.basePrice < 0) {
            globalData.listRewards
        } else globalData.listActivities
        val index = getIndexSavedReward(reward)
        if (index >= 0) {
            if (list[index].limitedTimes <= 1) noMore = true
            else list[index].limitedTimes--

            if (reward.basePrice < 0) globalData.listRewards = list
            else globalData.listActivities = list
        }
        return noMore
    }

    private fun getIndexSavedReward(theONE: Reward): Int {
        var founded = -1
        if (theONE.basePrice < 0) {
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
    private fun createRewardGoview(reward: Reward?) {
        saveData(globalData)
        val mandar = CreateInformation(
            reward != null,
            listOf(),
            reward
        )
        val intent = Intent(this, CreateReward::class.java)
        intent.putExtra(KEYpackage, Gson().toJson(mandar))
        startActivity(intent)
    }

    //DATA FUNCTIONS
    private fun sortAlphabetically(arrayList: List< Reward >): List< Reward >{
        Log.i("Paso", "AlphabeticalEnter")
        val retorno = arrayList.toMutableList()
        retorno.sortWith { o1: Reward, o2: Reward ->
            val first = if(o1.tagName == DEFAULTTAG) 'b' else 'a'
            val second = if(o2.tagName == DEFAULTTAG) 'b' else 'a'
            (first + o1.tagName + o1.name).compareTo(second + o2.tagName + o2.name)
        }
        return retorno
    }
    private fun sortByPoints(arrayList: List< Reward >, lettersUse: List<Char>): List< Reward >{
        Log.i("Paso", "Points enter")
        val retorno = arrayList.toMutableList()
        retorno.sortWith { o1: Reward, o2: Reward ->
            var txtPrice = Math.abs(o1.price).toString()
            var letter = lettersUse[if(txtPrice.length>10) 10 else txtPrice.length]
            var sepparateDefault = if(o1.tagName == DEFAULTTAG) 'b' else 'a'
            val compareA: String = sepparateDefault + o1.tagName + letter + txtPrice

            txtPrice = Math.abs(o2.price).toString()
            letter = lettersUse[if(txtPrice.length>10) 10 else txtPrice.length]
            sepparateDefault = if(o2.tagName == DEFAULTTAG) 'b' else 'a'
            val compareB: String = sepparateDefault + o2.tagName + letter + txtPrice
            Log.i("Paso", compareA)
            Log.i("Paso", compareB)
            compareA.compareTo(compareB)
        }
        return retorno
    }

    /*private fun Sort(option: Int, data: SaveFormat){
        val listReward = data.listRewards.toMutableList()
        val listActivity = data.listActivities.toMutableList()


        val sum2 = {input: MutableList<Reward> ->
           1
        }


    }*/

    private fun refresh() {
        //Los 2 son mismo, si cambia uno, cambiar el otro (Ver desp como hacer que "IT" sea Var instead of Val)
        globalData.listRewards.forEach {

            val today = LocalDateTime.now()

            if (it.discountRemoveAfter.devolver().isAfter(today)) {//Discount Vencio
                it.discountMOD = 1f
                it.discountRemoveAfter = MyOwnClock(LocalDateTime.MAX)
            }

            val dayPassed = today.isAfter(it.lastTimeUsed.devolver().plusDays(1))
            val hourPassed = today.isAfter(it.lastTimeUsed.devolver().plusHours(1))
            val duration = Duration.between(it.lastTimeUsed.devolver(), today)

            if (hourPassed && it.usagePercentageCount != 1f && !dayPassed) {
                //Hours but not days
                val removeTimes =
                    duration.toHours().toInt() - it.timesRemoved //How many more hours to remove
                it.timesRemoved = duration.toHours().toInt()
                val new = it.usagePercentageCount - (USAGEPORCENTAGEADD * removeTimes)
                it.usagePercentageCount = if (new > 1f) new else 1f

            } else if (dayPassed) {
                //Days passed
                if (duration.toDays().toInt() > howManyDaysToDiscount) {
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

            if (it.discountRemoveAfter.devolver().isAfter(today)) {//Discount Vencio
                it.discountMOD = 1f
                it.discountRemoveAfter = MyOwnClock(LocalDateTime.MAX)
            }

            val dayPassed = today.isAfter(it.lastTimeUsed.devolver().plusDays(1))
            val hourPassed = today.isAfter(it.lastTimeUsed.devolver().plusHours(1))
            val duration = Duration.between(it.lastTimeUsed.devolver(), today)

            if (hourPassed && it.usagePercentageCount != 1f && !dayPassed) {
                //Hours but not days
                val removeTimes =
                    duration.toHours().toInt() - it.timesRemoved //How many more hours to remove
                it.timesRemoved = duration.toHours().toInt()
                val new = it.usagePercentageCount - (USAGEPORCENTAGEADD * removeTimes)
                it.usagePercentageCount = if (new > 1f) new else 1f

            } else if (dayPassed) {
                //Days passed
                if (duration.toDays().toInt() > howManyDaysToDiscount) {
                    it.discountMOD = 1.25f //temp
                    it.discountRemoveAfter = MyOwnClock(today.plusDays(1))
                }
                it.usagePercentageCount = 1f
            }

            it.price =
                ((it.basePrice * it.discountMOD * it.usagePercentageCount) / 5).toInt() * 5
        }

        if (rewardSelected) {
            viewBinding.btnListActivities.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
            viewBinding.btnListRewards.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
            initRecyclerView(globalData.listRewards)
        } else {
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
            1.2f
        )
        //check if exist

        val str: String? = preferences.getString(KEY, "")
        if (!str.isNullOrBlank())
            saveFormat = Gson().fromJson(str, SaveFormat::class.java)

        return saveFormat
    }
}
//old code in case needed
/*
 //Return tags
private fun crearTags(lista: List<Reward>): List<String> {
    val retorno = mutableListOf<String>()
    lista.forEach {
        if (!retorno.contains(it.tagName))
            retorno += it.tagName
    }
    return retorno
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
 } */
