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
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rewardskotlin.adapter.RewardAdapter
import com.example.rewardskotlin.dataAndClasses.*
import com.example.rewardskotlin.databinding.ActivityMainBinding
import com.google.gson.Gson
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.abs

///TO-DO:
///      import export data
///      ADD DISCOUNTS
///      IMPLEMENT CONFIGURATION
/// LIMITED TIMES (implementado solo falta AVISAR)

//FIXED VALUES YOU CAN EDIT
private var USAGEPORCENTAGEADD = 0.25f
private var howManyDaysToDiscount = 2
private var DEBUGMODE = true //Desactiva o activa features

//--colors
private const val NOTSELECTEDCOLOR = "#ccbb8b"
private const val SELECTEDCOLOR = "#fcba03"

//SHARED KEYS [CHECK IF == IN OTHER ACTIVITY]
private const val DEFAULTTAG = "default"
private const val KEYsendAndGo = "recieve7"
private const val KEYpackage = "package7"

//KEYS
private const val SHARED = "Shared7"
private const val KEY = "MainKey7"
private const val FIRSTIME = "IsFirstTime7"

private val LETTERS = listOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k')

class MainActivity : AppCompatActivity() {
    //GLOBAL VAR
    private var rewardSelected = false
    private var optionSelected = 0
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
        initRecyclerView(globalData.listActivities)//TEMP

        viewBinding.btnListActivities.setOnClickListener {
            initRecyclerView(globalData.listActivities)//TEMP
            rewardSelected = false
            viewBinding.btnListActivities.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
            viewBinding.btnListRewards.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
        }
        viewBinding.btnListRewards.setOnClickListener {
            initRecyclerView(globalData.listRewards)//TEMP
            rewardSelected = true
            viewBinding.btnListActivities.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
            viewBinding.btnListRewards.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
        }


        //CREATE REWARD
        val jsonObtained: String? = intent.getStringExtra(KEYsendAndGo)
        if (!jsonObtained.isNullOrBlank()) {
            val obtained =
                Gson().fromJson(jsonObtained, SendBack::class.java)
            //new or editing an old one
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
        viewBinding.spinnerSort.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    var listaR = globalData.listRewards
                    var listaA = globalData.listActivities
                    when (position) {
                        0 -> {

                            listaR = sortAlphabetically(globalData.listRewards)
                            listaA = sortAlphabetically(globalData.listActivities)
                        }
                        1 -> {

                            listaR = sortByPoints(globalData.listRewards, LETTERS)
                            listaA = sortByPoints(globalData.listActivities, LETTERS)
                        }
                        2 -> {

                            listaR = sortByPoints(globalData.listRewards, LETTERS.reversed())
                            listaA = sortByPoints(globalData.listActivities, LETTERS.reversed())
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

        viewBinding.bottomBar.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.BottomList -> {
                    optionSelected = 0
                    refresh()
                    true
                }
                R.id.BottomAdd -> {
                    createRewardGoview(null)
                    true
                }
                R.id.BottomEdit -> {
                    optionSelected = 2
                    refresh()
                    true
                }
                R.id.BottomDelete -> {
                    optionSelected = 1
                    refresh()
                    true
                }
                R.id.BottomConfig -> {
                    DEBUGMODE = !DEBUGMODE
                    viewBinding.btnDebugAnything.isVisible = DEBUGMODE
                    viewBinding.btnRemoveUsage.isVisible = DEBUGMODE
                    viewBinding.btnDeleteAll.isVisible = DEBUGMODE
                    true
                }
                else -> false
            }
        }
        ///- - - - - - - DEBUG FEATURES
        viewBinding.btnDebugAnything.isVisible = DEBUGMODE
        viewBinding.btnRemoveUsage.isVisible = DEBUGMODE
        viewBinding.btnDeleteAll.isVisible = DEBUGMODE

        if (DEBUGMODE) {
            viewBinding.btnDebugAnything.setOnClickListener {
                Log.i("ver", Gson().toJson(globalData.listActivities.map { it.name } + globalData.listRewards.map { it.name }))
            }
            viewBinding.btnDeleteAll.setOnClickListener {
                globalData.listRewards = emptyList()
                globalData.listActivities = emptyList()
                saveData(globalData)
                refresh()
            }
            viewBinding.btnRemoveUsage.setOnClickListener {
                globalData.listRewards.forEach {
                    it.usagePercentageCount = 1f
                }
                globalData.listActivities.forEach {
                    it.usagePercentageCount = 1f
                }
                refresh()
            }
        }
        //END OF DEBUG FEATURES / / / / / /

        refresh() //LASTLY
    }

    ////FUNCTIONS////

    //Recycler View
    private fun initRecyclerView(lista: List<Reward>){//, option: Int) {
        viewBinding.listOfRewards.layoutManager = LinearLayoutManager(this)
        viewBinding.listOfRewards.adapter = RewardAdapter(lista, getTags(), optionSelected) { reward ->
            onItemSelected(
                reward
            )
        }
    }

    private fun onItemSelected(clickedReward: OnClickReturn) {
        //Not modify nor delete
        if(!clickedReward.isDelete && !clickedReward.isEdit){
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
        //modify
        if (clickedReward.isEdit && !clickedReward.isDelete) {
            createRewardGoview(clickedReward.reward)
        }
        //delete
        if(!clickedReward.isEdit && clickedReward.isDelete)
        deleteReward(clickedReward.reward)

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
        //AlreadyExistingNames
        val mandar = CreateInformation(
            isEdit = reward != null,
            tags = getTags(),
            existingNames = globalData.listActivities.map { it.name.lowercase() } + globalData.listRewards.map { it.name.lowercase() },
            reward = reward
        )
        val intent = Intent(this, CreateReward::class.java)
        intent.putExtra(KEYpackage, Gson().toJson(mandar))
        startActivity(intent)
    }

    //DATA FUNCTIONS
    private fun sortAlphabetically(arrayList: List<Reward>): List<Reward> {
        val retorno = arrayList.toMutableList()
        retorno.sortWith { o1: Reward, o2: Reward ->
            val first = if (o1.tagName == DEFAULTTAG) 'b' else 'a'
            val second = if (o2.tagName == DEFAULTTAG) 'b' else 'a'
            (first + o1.tagName + o1.name).compareTo(second + o2.tagName + o2.name)
        }
        return retorno
    }

    private fun sortByPoints(arrayList: List<Reward>, lettersUse: List<Char>): List<Reward> {
        val retorno = arrayList.toMutableList()
        retorno.sortWith { o1: Reward, o2: Reward ->
            var txtPrice = abs(o1.price).toString()
            var letter = lettersUse[if (txtPrice.length > 10) 10 else txtPrice.length]
            var sepparateDefault = if (o1.tagName == DEFAULTTAG) 'b' else 'a'
            val compareA: String = sepparateDefault + o1.tagName + letter + txtPrice

            txtPrice = abs(o2.price).toString()
            letter = lettersUse[if (txtPrice.length > 10) 10 else txtPrice.length]
            sepparateDefault = if (o2.tagName == DEFAULTTAG) 'b' else 'a'
            val compareB: String = sepparateDefault + o2.tagName + letter + txtPrice
            compareA.compareTo(compareB)
        }
        return retorno
    }

    private fun getTags(): List<String>{
        val tagsList: MutableList<String> = mutableListOf()
        globalData.listActivities.forEach {
            if (!tagsList.contains(it.tagName))
                tagsList.add(it.tagName)
        }
        globalData.listRewards.forEach {
            if (!tagsList.contains(it.tagName))
                tagsList.add(it.tagName)
        }
        tagsList.sortBy { it }
        tagsList.remove(DEFAULTTAG)
        return mutableListOf(DEFAULTTAG) + tagsList
    }

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
            initRecyclerView(globalData.listRewards)//TEMP)
        } else {
            viewBinding.btnListActivities.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
            viewBinding.btnListRewards.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
            initRecyclerView(globalData.listActivities)//TEMP)
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
