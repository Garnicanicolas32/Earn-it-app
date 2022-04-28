package com.example.rewardskotlin

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.rewardskotlin.dataAndClasses.Reward
import com.example.rewardskotlin.databinding.ActivityCreateRewardBinding
import com.google.gson.Gson

class CreateReward : AppCompatActivity() {

    //GLOBAL VARS
    private val NUMEROBASE = 100f //Vewy importent
    private var perMonthMultiplie = 30

    private val listRewardMOD = listOf(0.95f,1f,1.5f)
    private val listActivitiesMOD = listOf(1f,1.25f,1.75f)

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

        //REWARD OR ACTIVITY CHOOSEN
        viewBinding.btnChooseActivty.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
        viewBinding.btnchooseReward.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))

        viewBinding.btnChooseActivty.setOnClickListener {
            isReward = false
            viewBinding.getPrioridad.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.Actividad)
            )
            viewBinding.btnChooseActivty.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
            viewBinding.btnchooseReward.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
        }
        viewBinding.btnchooseReward.setOnClickListener {
            isReward = true
            viewBinding.getPrioridad.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                resources.getStringArray(R.array.Reward)
            )
            viewBinding.btnChooseActivty.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
            viewBinding.btnchooseReward.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
        }

        //IS LIMITED LISTENER
        viewBinding.isLimited.setOnCheckedChangeListener { _, isChecked ->
            viewBinding.txtLimitedTimes.isVisible = isChecked
        }

        //DAY WEEK MONTH BUTTONS
        viewBinding.btnDay.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
        viewBinding.btnWeek.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
        viewBinding.btnMonth.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))

        viewBinding.btnDay.setOnClickListener {
            perMonthMultiplie = 30
            viewBinding.btnDay.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
            viewBinding.btnWeek.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
            viewBinding.btnMonth.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
        }
        viewBinding.btnWeek.setOnClickListener {
            perMonthMultiplie = 4
            viewBinding.btnDay.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
            viewBinding.btnWeek.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
            viewBinding.btnMonth.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
        }
        viewBinding.btnMonth.setOnClickListener {
            perMonthMultiplie = 1
            viewBinding.btnDay.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
            viewBinding.btnWeek.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
            viewBinding.btnMonth.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
        }

        //SUBMIT AND GO
        viewBinding.btnSubmit.setOnClickListener {
            if(checkIFok()){
                sendAndGo(Gson().toJson(createReward()))
            }
        }
    }

    //FUNCTIONS

    private fun sendAndGo(json: String){
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("NewReward", json)
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
        val points: Float = NUMEROBASE * modPrioridad * timesPerMonthMOD * if(isReward) -1f else 1f

        // Create variable
        return Reward(
            viewBinding.txtNombre.text.toString(),
            points.toInt(),
            points,
            isReward,
            false,
            viewBinding.isLimited.isChecked,
            limitedtimes,
            1f, //temp?
            0.25f, //temp?
            modPrioridad,
            timesPerMonthMOD,
            1f,
            -1,
            "default" //temp
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