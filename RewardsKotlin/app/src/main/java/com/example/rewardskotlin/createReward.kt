package com.example.rewardskotlin

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.rewardskotlin.DataAndClasses.reward
import com.example.rewardskotlin.databinding.ActivityCreateRewardBinding
import com.google.gson.Gson

class createReward : AppCompatActivity() {

    private val REWARDlista = listOf<Float>(1f,2f,3f)
    private val ACTIVDADlista = listOf<Float>(1f,2f,3f)
    private val ERRORCOLOR = "#c71616"
    private val NOTSELECTEDCOLOR = "#b0a78f"
    private val SELECTEDCOLOR = "#ffbc00"
    private var CreandoTag = false
    private var isReward = true
    private lateinit var accesoView: ActivityCreateRewardBinding

    private var perMonthMultiplie = 30;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accesoView = ActivityCreateRewardBinding.inflate(layoutInflater)
        setContentView(accesoView.root)

        //submit
        accesoView.btnSubmit.setOnClickListener {
            if(CheckIfOK()){
                Send(Gson().toJson(Create()))
            }
        }
        //Reward or activity
        accesoView.btnChooseActivty.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
        accesoView.btnchooseReward.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))

        accesoView.btnChooseActivty.setOnClickListener {
            isReward = false
            accesoView.getPrioridad.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.Actividad)
            )
            accesoView.btnChooseActivty.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
            accesoView.btnchooseReward.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
        }
        accesoView.btnchooseReward.setOnClickListener {
            isReward = true
            accesoView.getPrioridad.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                resources.getStringArray(R.array.Reward)
            )
            accesoView.btnChooseActivty.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
            accesoView.btnchooseReward.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
        }

        //Is limited
        accesoView.isLimited.setOnCheckedChangeListener { _, isChecked ->
            accesoView.txtLimitedTimes.isVisible = isChecked
        }

        //Day week month
        accesoView.btnDay.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
        accesoView.btnWeek.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
        accesoView.btnMonth.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))

        accesoView.btnDay.setOnClickListener {
            perMonthMultiplie = 30
            accesoView.btnDay.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
            accesoView.btnWeek.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
            accesoView.btnMonth.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
        }
        accesoView.btnWeek.setOnClickListener {
            perMonthMultiplie = 4
            accesoView.btnDay.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
            accesoView.btnWeek.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
            accesoView.btnMonth.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
        }
        accesoView.btnMonth.setOnClickListener {
            perMonthMultiplie = 1
            accesoView.btnDay.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
            accesoView.btnWeek.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
            accesoView.btnMonth.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
        }

        //accesoView.getPrioridad.selectedItemPosition
        // val a = intent.getStringExtra("newReward")
    }

    private fun Send(json: String){
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("NewReward", json)
        startActivity(intent)
    }
    private fun Create(): reward {
        //PrioridadTiempoGasta
        var modPrioridad = if (isReward)
            REWARDlista[accesoView.getPrioridad.selectedItemPosition]
        else
            ACTIVDADlista[accesoView.getPrioridad.selectedItemPosition]

        //limited times
        var limitedtimes = 1
        if (accesoView.isLimited.isChecked) {
            try {
                limitedtimes = accesoView.txtLimitedTimes.text.toString().toInt()
            } catch (nfe: NumberFormatException) {
                limitedtimes = 1
            }
        }
        //times per month
        var timesPerMonth = 1
        try {
            timesPerMonth = accesoView.txtLimitedTimes.text.toString().toInt() * perMonthMultiplie
        } catch (nfe: NumberFormatException) {
            limitedtimes = 1
        }
        val points = 100 * if(isReward) -1 else 1

        val a = 1
        val b = 1
        var igual = a == b

        // Create variable
        return reward(
            accesoView.txtNombre.text.toString(),
            points, //temp
            isReward,
            false,
            accesoView.isLimited.isChecked,
            limitedtimes,
            0f,
            0.25f, //temp?
            modPrioridad,
            30 / timesPerMonth.toFloat(),
            1f,
            -1,
            "default" //temp
        )
    }
    private fun CheckIfOK(): Boolean {
        var retorno = true
        if (accesoView.txtNombre.text.trim().isBlank()) {
            accesoView.txtNombre.setBackgroundColor(Color.parseColor(ERRORCOLOR))
            retorno = false
        } else accesoView.txtNombre.setBackgroundColor(0x00000000)

        if (accesoView.isLimited.isChecked && accesoView.txtLimitedTimes.text.trim().isBlank()) {
            accesoView.txtLimitedTimes.setBackgroundColor(Color.parseColor(ERRORCOLOR))
            retorno = false
        } else accesoView.txtLimitedTimes.setBackgroundColor(0x00000000)

        if (accesoView.txtTimesPerMonth.text.trim().isBlank()) {
            accesoView.txtTimesPerMonth.setBackgroundColor(Color.parseColor(ERRORCOLOR))
            retorno = false
        } else accesoView.txtTimesPerMonth.setBackgroundColor(0x00000000)

        if (CreandoTag && accesoView.txtNombreTag.text.trim().isBlank()) {
            accesoView.txtNombreTag.setBackgroundColor(Color.parseColor(ERRORCOLOR))
            retorno = false
        } else accesoView.txtNombreTag.setBackgroundColor(0x00000000)

        return retorno
    }
}