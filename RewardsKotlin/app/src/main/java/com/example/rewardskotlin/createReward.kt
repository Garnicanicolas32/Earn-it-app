package com.example.rewardskotlin

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import com.example.rewardskotlin.dataAndClasses.CreateInformation
import com.example.rewardskotlin.dataAndClasses.MyOwnClock
import com.example.rewardskotlin.dataAndClasses.Reward
import com.example.rewardskotlin.dataAndClasses.SendBack
import com.example.rewardskotlin.databinding.ActivityCreateRewardBinding
import com.google.gson.Gson
import java.time.LocalDateTime

//FIXED VALUES YOU CAN EDIT
private const val NUMEROBASE = 100f //Vewy importent
private val listRewardMOD = listOf(0.95f, 1f, 1.25f)
private val listActivitiesMOD = listOf(1f, 1.25f, 1.75f)

//SHARED KEYS [CHECK IF == IN OTHER ACTIVITY]
private const val DEFAULTTAG = "default"
private const val KEYsendAndGo = "recieve7"
private const val KEYpackage = "package7"
//--Colors
private const val CORRECTCOLOR = "#94d162"
private const val ERRORCOLOR = "#c71616"
private const val NOTSELECTEDCOLOR = "#ccbb8b" //"#b0a78f"
private const val SELECTEDCOLOR = "#B6C454" //"#ffbc00"

class CreateReward : AppCompatActivity() {
    //GLOBAL VARS
    private var perMonthMultiplie = 30
    private var dayWeekMonth = 1
    private var isReward = true

    //LATEINIT VARS
    private lateinit var viewBinding: ActivityCreateRewardBinding

    //---------------ON CREATE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //INFLATE BINDING
        viewBinding = ActivityCreateRewardBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val jsonObtained = intent.getStringExtra(KEYpackage) + "" //SAFE IN CASE ERROR
        val obtained = if(jsonObtained.trim().isBlank())
            CreateInformation(
                false,
                listOf(DEFAULTTAG),
                listOf(),
                null
            )
            else
            Gson().fromJson(jsonObtained, CreateInformation::class.java)

        //IF EDITING REWARD
        if (obtained.isEdit){
            val newRewardObtained: Reward = obtained.reward!!
            // name
            viewBinding.txtNombre.setText(newRewardObtained.name)
            //is limited
            if (newRewardObtained.limitedTimes > -1) {
                viewBinding.isLimited.isChecked = true
                viewBinding.txtLimitedTimes.isVisible = true
                viewBinding.txtLimitedTimes.setText(newRewardObtained.limitedTimes.toString())
            }
            //reward or activity // importance mod
            if (newRewardObtained.basePrice < 0)
                switchRewardOrActivity(1)
            else
                switchRewardOrActivity(2)
            //day week month
            switchDayWeekMonth(newRewardObtained.options[0])
            viewBinding.txtTimesPerMonth.setText(newRewardObtained.options[1].toString())
            //spinner
            viewBinding.getPrioridad.setSelection(newRewardObtained.options[2])
            //tag name
            viewBinding.txtNombreTag.setText(newRewardObtained.tagName)
        }
        else{
            switchRewardOrActivity(1)
            switchDayWeekMonth(1) //default
        }

        //REWARD OR ACTIVITY CHOOSEN
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
        viewBinding.btnDay.setOnClickListener {
            switchDayWeekMonth(1)
        }
        viewBinding.btnWeek.setOnClickListener {
            switchDayWeekMonth(2)
        }
        viewBinding.btnMonth.setOnClickListener {
            switchDayWeekMonth(3)
        }
        //DELETE
        viewBinding.btnDelete.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
        }
        //TAG SPINNER
        val dataAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_dropdown_item, obtained.tags
        )
        viewBinding.spinnerGetTag.adapter = dataAdapter

        //SUBMIT AND GO
        viewBinding.btnSubmit.setOnClickListener {
            if (checkIFok(obtained)) {
                val send = SendBack(
                    obtained.isEdit,
                    createReward(),
                    if (obtained.isEdit) obtained.reward else null
                )
                sendAndGo(Gson().toJson(send))
            }
        }
    }

    //---------------FUNCTIONS

    //SWITCHS
    private fun switchDayWeekMonth(option: Int) {
        when (option) {
            //DAY
            1 -> {
                dayWeekMonth = 1
                perMonthMultiplie = 30
                changeColor(viewBinding.btnDay.background, SELECTEDCOLOR)
                changeColor(viewBinding.btnWeek.background, NOTSELECTEDCOLOR)
                changeColor(viewBinding.btnMonth.background, NOTSELECTEDCOLOR)
                //viewBinding.btnDay.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
                //viewBinding.btnWeek.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
                //viewBinding.btnMonth.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
            }
            //WEEK
            2 -> {
                dayWeekMonth = 2
                perMonthMultiplie = 4
                changeColor(viewBinding.btnDay.background, NOTSELECTEDCOLOR)
                changeColor(viewBinding.btnWeek.background, SELECTEDCOLOR)
                changeColor(viewBinding.btnMonth.background, NOTSELECTEDCOLOR)
                //viewBinding.btnDay.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
                //viewBinding.btnWeek.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
                //viewBinding.btnMonth.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
            }
            //MONTH
            3 -> {
                dayWeekMonth = 3
                perMonthMultiplie = 1
                changeColor(viewBinding.btnDay.background, NOTSELECTEDCOLOR)
                changeColor(viewBinding.btnWeek.background, NOTSELECTEDCOLOR)
                changeColor(viewBinding.btnMonth.background, SELECTEDCOLOR)
                //viewBinding.btnDay.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
                //viewBinding.btnWeek.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
                //viewBinding.btnMonth.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
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
                viewBinding.btnChooseActivty.background = changeColor(viewBinding.btnChooseActivty.background, NOTSELECTEDCOLOR)
                viewBinding.btnchooseReward.background = changeColor(viewBinding.btnchooseReward.background, SELECTEDCOLOR)
                //viewBinding.btnChooseActivty.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
                //viewBinding.btnchooseReward.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
            }
            //Activity
            2 -> {
                isReward = false
                viewBinding.getPrioridad.adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    resources.getStringArray(R.array.Actividad)
                )
                viewBinding.btnChooseActivty.background = changeColor(viewBinding.btnChooseActivty.background, SELECTEDCOLOR)
                viewBinding.btnchooseReward.background = changeColor(viewBinding.btnchooseReward.background, NOTSELECTEDCOLOR)
                //viewBinding.btnChooseActivty.setBackgroundColor(Color.parseColor(SELECTEDCOLOR))
                //viewBinding.btnchooseReward.setBackgroundColor(Color.parseColor(NOTSELECTEDCOLOR))
            }
        }
    }

    //DATA
    private fun sendAndGo(json: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(KEYsendAndGo, json)
        startActivity(intent)
    }

    private fun createReward(): Reward {
        //PrioridadTiempoGasta
        val modPrioridad = if (isReward)
            listRewardMOD[viewBinding.getPrioridad.selectedItemPosition]
        else
            listActivitiesMOD[viewBinding.getPrioridad.selectedItemPosition]
        //limited times
        var limitedtimes = -1
        if (viewBinding.isLimited.isChecked) {
            limitedtimes = try {
                viewBinding.txtLimitedTimes.text.toString().toInt()
            } catch (nfe: NumberFormatException) {
                -1
            }
        }
        //times per month
        var timesPerMonth = 1
        try {
            timesPerMonth = viewBinding.txtTimesPerMonth.text.toString().toInt() * perMonthMultiplie
        } catch (nfe: NumberFormatException) {

        }
        val timesPerMonthMOD: Float = 30f / timesPerMonth.toFloat()
        //Base reward
        val points: Float = NUMEROBASE * modPrioridad * timesPerMonthMOD * if (isReward) -1f else 1f
        val now = MyOwnClock(LocalDateTime.now())
        // Create variable
        return Reward(
            viewBinding.txtNombre.text.toString(),
            points.toInt(),
            points,
            limitedtimes,
            1f,
            listOf(
                dayWeekMonth,
                viewBinding.txtTimesPerMonth.text.toString().toInt(),
                viewBinding.getPrioridad.selectedItemPosition
            ),
            1f,
            now,
            viewBinding.txtNombreTag.text.toString(), //"default", //temp
            now,
            0
        )
    }

    private fun checkIFok(obtained: CreateInformation): Boolean {
        var retorno = true

        val text = viewBinding.txtNombre.text.toString().lowercase()
        val lista = obtained.existingNames.toMutableList()
        if(obtained.isEdit)
        lista.remove(obtained.reward!!.name)

        if (text.trim().isBlank() or lista.contains(text)) {
            viewBinding.txtNombre.background = changeStroke(viewBinding.txtNombre.background, ERRORCOLOR)
            retorno = false
        }else viewBinding.txtNombre.background = changeStroke(viewBinding.txtNombre.background, CORRECTCOLOR)
            //viewBinding.txtNombre.setBackgroundColor(Color.parseColor(CORRECTCOLOR))

        if (viewBinding.isLimited.isChecked && viewBinding.txtLimitedTimes.text.trim().isBlank()) {
            viewBinding.txtLimitedTimes.background = changeStroke(viewBinding.txtLimitedTimes.background, ERRORCOLOR)
            retorno = false
        } else viewBinding.txtLimitedTimes.background = changeStroke(viewBinding.txtLimitedTimes.background, CORRECTCOLOR)
            //viewBinding.txtLimitedTimes.setBackgroundColor(Color.parseColor(CORRECTCOLOR))

        if (viewBinding.txtTimesPerMonth.text.trim().isBlank()) {
            viewBinding.txtTimesPerMonth.background = changeStroke(viewBinding.txtTimesPerMonth.background, ERRORCOLOR)
            retorno = false
        } else viewBinding.txtTimesPerMonth.background = changeStroke(viewBinding.txtTimesPerMonth.background, CORRECTCOLOR)
            //viewBinding.txtTimesPerMonth.setBackgroundColor(Color.parseColor(CORRECTCOLOR))

        if (viewBinding.txtNombreTag.text.trim().isBlank()) {
            viewBinding.txtNombreTag.background = changeStroke(viewBinding.txtNombreTag.background, ERRORCOLOR)
            retorno = false
        } else viewBinding.txtNombreTag.background = changeStroke(viewBinding.txtNombreTag.background, CORRECTCOLOR)
        //viewBinding.txtNombreTag.setBackgroundColor(Color.parseColor(CORRECTCOLOR))

        return retorno
    }

    private fun changeStroke(background: Drawable, color: String): Drawable{
        (background as? GradientDrawable)?.setStroke(3, Color.parseColor(color))
        return background
    }

    private fun changeColor(backg: Drawable, color: String): Drawable {
        val drawable = DrawableCompat.wrap(backg)
        DrawableCompat.setTint(drawable, Color.parseColor(color))

        return drawable
    }
}