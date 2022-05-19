package com.example.rewardskotlin

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.rewardskotlin.dataAndClasses.MyOwnClock
import com.example.rewardskotlin.dataAndClasses.Reward
import com.example.rewardskotlin.databinding.ActivityCreateRewardBinding
import com.google.gson.Gson
import java.time.LocalDateTime

class CreateReward : AppCompatActivity() {

    //GLOBAL VARS
    private var editing = false
    private var oldOne = ""

    private val NUMEROBASE = 100f //Vewy importent
    private var perMonthMultiplie = 30
    private var dayWeekMonth = 1

    private val listRewardMOD = listOf(0.95f, 1f, 1.5f)
    private val listActivitiesMOD = listOf(1f, 1.25f, 1.75f)

    private var isNewTag = false
    private var isReward = true

    //LATEINIT VARS
    private lateinit var viewBinding: ActivityCreateRewardBinding

    //COLORS
    private val ERRORCOLOR = "#c71616"
    private val NOTSELECTEDCOLOR = "#b0a78f"
    private val SELECTEDCOLOR = "#ffbc00"

    //ON CREATE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //INFLATE BINDING
        viewBinding = ActivityCreateRewardBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        //IF EDITING REWARD
        if (!intent.getStringExtra("ModifyRewardKEY").isNullOrBlank()) {
            oldOne = intent.getStringExtra("ModifyRewardKEY") + ""
            val newRewardObtained =
                Gson().fromJson(oldOne, Reward::class.java)

            //newRewardObtained.isModify = false
            oldOne = Gson().toJson(newRewardObtained)

            editing = true
            // name
            viewBinding.txtNombre.setText(newRewardObtained.name)
            //is limited
            if (newRewardObtained.isLimited) {
                viewBinding.isLimited.isChecked = true
                viewBinding.txtLimitedTimes.isVisible = true
                viewBinding.txtLimitedTimes.setText(newRewardObtained.limitedTimes.toString())
            }
            //reward or activity // importance mod
            var browseList = listActivitiesMOD
            if (newRewardObtained.isReward) {
                switchRewardOrActivity(1)
                browseList = listRewardMOD
            } else
                switchRewardOrActivity(2)

            //day week month
            //Toast.makeText(this,newRewardObtained.dayWeekMonthOption.toString(),Toast.LENGTH_LONG).show()
            switchDayWeekMonth(newRewardObtained.dayWeekMonthOption)
            viewBinding.txtTimesPerMonth.setText(newRewardObtained.timesPerX.toString())

            //spinner
            var counter = 0
            browseList.forEach {
                if (it == newRewardObtained.prioridadMOD && counter < viewBinding.getPrioridad.adapter.count)
                    viewBinding.getPrioridad.setSelection(counter)
                counter++
            }
        }

        //REWARD OR ACTIVITY CHOOSEN
        if (!editing)
            switchRewardOrActivity(1)
        viewBinding.btnChooseActivty.setOnClickListener {
            switchRewardOrActivity(2)
        }
        viewBinding.btnchooseReward.setOnClickListener {
            switchRewardOrActivity(1)
        }

        //IS LIMITED LISTENER
        viewBinding.isLimited.setOnCheckedChangeListener { _, isChecked ->
            viewBinding.txtLimitedTimes.isVisible = isChecked
        }

        //DAY WEEK MONTH BUTTONS
        if (!editing)
            switchDayWeekMonth(1) //default
        viewBinding.btnDay.setOnClickListener {
            switchDayWeekMonth(1)
        }
        viewBinding.btnWeek.setOnClickListener {
            switchDayWeekMonth(2)
        }
        viewBinding.btnMonth.setOnClickListener {
            switchDayWeekMonth(3)
        }

        //SUBMIT AND GO
        viewBinding.btnSubmit.setOnClickListener {
            val json1 = Gson().toJson(createReward())
            if (checkIFok()) {
                if (editing) {
                    sendAndGo(json1, oldOne)
                }
                else
                    sendAndGo(json1)
            }
        }

        //DELETE
        viewBinding.btnDelete.setOnClickListener {
            if (editing) { //If delete editing
                val newRewardObtained =
                    Gson().fromJson(intent.getStringExtra("ModifyRewardKEY") + "", Reward::class.java)
                newRewardObtained.isDelete = true
                sendAndGo(Gson().toJson(newRewardObtained))
            } else { //If delete a new one
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    //FUNCTIONS

    //SWITCHS
    private fun switchDayWeekMonth(option: Int) {
        when (option) {
            //DAY
            1 -> {
                dayWeekMonth = 1
                perMonthMultiplie = 30
                viewBinding.btnDay.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
                viewBinding.btnWeek.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
                viewBinding.btnMonth.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
            }
            //WEEK
            2 -> {
                dayWeekMonth = 2
                perMonthMultiplie = 4
                viewBinding.btnDay.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
                viewBinding.btnWeek.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
                viewBinding.btnMonth.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
            }
            //MONTH
            3 -> {
                dayWeekMonth = 3
                perMonthMultiplie = 1
                viewBinding.btnDay.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
                viewBinding.btnWeek.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
                viewBinding.btnMonth.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
            }
        }
    }

    private fun switchRewardOrActivity(option: Int) {
        when (option) {
            //Reward
            1 -> {
                isReward = true
                viewBinding.getPrioridad.adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    resources.getStringArray(R.array.Reward)
                )
                viewBinding.btnChooseActivty.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
                viewBinding.btnchooseReward.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
            }
            //Activity
            2 -> {
                isReward = false
                viewBinding.getPrioridad.adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    resources.getStringArray(R.array.Actividad)
                )
                viewBinding.btnChooseActivty.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
                viewBinding.btnchooseReward.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
            }
        }
    }

    //DATA
    private fun sendAndGo(json: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("NewReward", json)
        startActivity(intent)
    }

    private fun sendAndGo(json: String, json2: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("NewReward", json)
        intent.putExtra("isChangeAndDelete", true)
        intent.putExtra("OldObject", json2)
        startActivity(intent)
    }

    private fun createReward(): Reward {
        //PrioridadTiempoGasta
        val modPrioridad = if (isReward)
            listRewardMOD[viewBinding.getPrioridad.selectedItemPosition]
        else
            listActivitiesMOD[viewBinding.getPrioridad.selectedItemPosition]

        //limited times
        var limitedtimes = 1
        if (viewBinding.isLimited.isChecked) {
            limitedtimes = try {
                viewBinding.txtLimitedTimes.text.toString().toInt()
            } catch (nfe: NumberFormatException) {
                1
            }
        }
        //times per month
        var timesPerMonth = 1
        try {
            timesPerMonth = viewBinding.txtTimesPerMonth.text.toString().toInt() * perMonthMultiplie
        } catch (nfe: NumberFormatException) {
            limitedtimes = 2
        }
        val timesPerMonthMOD: Float = 30f / timesPerMonth.toFloat()

        //Base reward
        //val points = 100 * if(isReward) -1 else 1 //temporal
        val points: Float = NUMEROBASE * modPrioridad * timesPerMonthMOD * if (isReward) -1f else 1f

        val now = MyOwnClock(LocalDateTime.now())
        //Toast.makeText(this, now.toString(), Toast.LENGTH_SHORT).show()
        // Create variable
        return Reward(
            viewBinding.txtNombre.text.toString(),
            points.toInt(),
            points,
            isReward,
            false,
            false,
            viewBinding.isLimited.isChecked,
            limitedtimes,
            1f,
            0.25f, //temp?
            modPrioridad,
            timesPerMonthMOD,
            dayWeekMonth,
            viewBinding.txtTimesPerMonth.text.toString().toInt(),
            1f,
            now,
            "default", //temp
            now,
            0
        )
    }

    private fun checkIFok(): Boolean {
        var retorno = true
        if (viewBinding.txtNombre.text.trim().isBlank()) {
            viewBinding.txtNombre.setBackgroundColor(Color.parseColor(ERRORCOLOR))
            retorno = false
        } else viewBinding.txtNombre.setBackgroundColor(0x00000000)

        if (viewBinding.isLimited.isChecked && viewBinding.txtLimitedTimes.text.trim().isBlank()) {
            viewBinding.txtLimitedTimes.setBackgroundColor(Color.parseColor(ERRORCOLOR))
            retorno = false
        } else viewBinding.txtLimitedTimes.setBackgroundColor(0x00000000)

        if (viewBinding.txtTimesPerMonth.text.trim().isBlank()) {
            viewBinding.txtTimesPerMonth.setBackgroundColor(Color.parseColor(ERRORCOLOR))
            retorno = false
        } else viewBinding.txtTimesPerMonth.setBackgroundColor(0x00000000)

        if (isNewTag && viewBinding.txtNombreTag.text.trim().isBlank()) {
            viewBinding.txtNombreTag.setBackgroundColor(Color.parseColor(ERRORCOLOR))
            retorno = false
        } else viewBinding.txtNombreTag.setBackgroundColor(0x00000000)

        return retorno
    }
}