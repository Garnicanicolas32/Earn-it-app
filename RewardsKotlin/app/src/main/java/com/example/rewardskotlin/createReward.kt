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
private const val KEYsendAndGo = "recieve8"
private const val KEYpackage = "package8"

//--Colors
private const val CORRECTCOLOR = "#94d162"
private const val ERRORCOLOR = "#c71616"
private const val NOTSELECTEDCOLOR = "#ccbb8b" //"#b0a78f"
private const val SELECTEDCOLOR = "#B6C454" //"#ffbc00"
private const val TRANSPAREN = "#FF1A6300"

class CreateReward : AppCompatActivity() {  //  TODO : USAGE REWORK : LANDSCAPE MODE :
    //GLOBAL VARS
    private var perMonthMultiplie = 30
    private var dayWeekMonth = 1
    private var isReward = true
    private var popup = false

    //LATEINIT VARS
    private lateinit var viewBinding: ActivityCreateRewardBinding

    //---------------ON CREATE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //INFLATE BINDING
        viewBinding = ActivityCreateRewardBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val jsonObtained = intent.getStringExtra(KEYpackage) + "" //SAFE IN CASE ERROR
        val obtained = if (jsonObtained.trim().isBlank())
            CreateInformation(
                false,
                listOf(DEFAULTTAG),
                listOf(),
                null
            )
        else
            Gson().fromJson(jsonObtained, CreateInformation::class.java)

        changeStroke(viewBinding.txtTimesPerMonth.background, TRANSPAREN)
        changeStroke(viewBinding.txtLimitedTimes.background, TRANSPAREN)
        changeStroke(viewBinding.insertNombre.background, TRANSPAREN)
        changeStroke(viewBinding.txtCreateTag.background, TRANSPAREN)

        //TAG SPINNER
        val list = obtained.tags.toMutableList()
        if(list.isEmpty())
            list.add("No tag")
        else
            list[0] = "No tag"

        val dataAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_dropdown_item, list
        )
        viewBinding.spinnerGetTag.adapter = dataAdapter

        //IF EDITING REWARD
        if (obtained.isEdit) {
            val newRewardObtained: Reward = obtained.reward!!
            // name
            viewBinding.insertNombre.setText(newRewardObtained.name)
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
            val i = obtained.tags.indexOf(obtained.reward!!.tagName)
            if (i >= 0 && obtained.reward!!.tagName != DEFAULTTAG) viewBinding.spinnerGetTag.setSelection(
                i
            )
            else if (obtained.reward!!.tagName == DEFAULTTAG)
                viewBinding.spinnerGetTag.setSelection(0)
        } else {
            switchRewardOrActivity(1)
            switchDayWeekMonth(1) //default
        }


        viewBinding.btnAddTag.setOnClickListener {
            if (!popup) switchCreateTag(true)
        }

        viewBinding.btnCancelCreateTag.setOnClickListener {
            switchCreateTag(false)
        }

        //REWARD OR ACTIVITY CHOOSEN
        viewBinding.btnChooseActivty.setOnClickListener {
            if (!popup) switchRewardOrActivity(2)
        }
        viewBinding.btnchooseReward.setOnClickListener {
            if (!popup) switchRewardOrActivity(1)
        }

        //IS LIMITED LISTENER
        viewBinding.isLimited.setOnCheckedChangeListener { _, isChecked ->
            if (!popup) viewBinding.txtLimitedTimes.isVisible = isChecked
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
            if (!popup) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        //NEW TAG
        viewBinding.btnCreateTag.setOnClickListener {
            var pass = viewBinding.txtCreateTag.text.toString().isNotBlank()
            val text = viewBinding.txtCreateTag.text.toString().lowercase()
            if (pass){
                pass =
                    !obtained.tags.contains(viewBinding.txtCreateTag.text.toString()) && viewBinding.txtCreateTag.text.toString() != DEFAULTTAG.lowercase()
            viewBinding.alreadyExist.isVisible = !pass}
            if (pass) {
                list.add(text)
                viewBinding.spinnerGetTag.adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item, list
                )
                switchCreateTag(false)
                viewBinding.spinnerGetTag.setSelection(list.indexOf(text))
            } else viewBinding.txtCreateTag.background =
                changeStroke(viewBinding.txtCreateTag.background, ERRORCOLOR)

        }

        //SUBMIT AND GO
        viewBinding.btnSubmit.setOnClickListener {
            if (!popup) {
                if (checkIFok(obtained)) {
                    val reward = createReward()
                    var same = false
                    if (obtained.isEdit) {
                        same = !listOf(
                            reward.basePrice == obtained.reward!!.basePrice,
                            reward.limitedTimes == obtained.reward!!.limitedTimes,
                            reward.name == obtained.reward!!.name,
                            reward.options == obtained.reward!!.options,
                            reward.tagName == obtained.reward!!.tagName
                        ).contains(false)
                    }
                    if (!same) {
                        val send = SendBack(
                            obtained.isEdit,
                            reward,
                            if (obtained.isEdit) obtained.reward else null
                        )
                        sendAndGo(Gson().toJson(send))
                    } else {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    //---------------FUNCTIONS

    //SWITCHS
    private fun switchDayWeekMonth(option: Int) {
        if (!popup) {
            when (option) {
                //DAY
                1 -> {
                    dayWeekMonth = 1
                    perMonthMultiplie = 30
                    viewBinding.txtTimesPerMonth.hint = "Per day"
                    changeColor(viewBinding.btnDay.background, SELECTEDCOLOR)
                    changeColor(viewBinding.btnWeek.background, NOTSELECTEDCOLOR)
                    changeColor(viewBinding.btnMonth.background, NOTSELECTEDCOLOR)
                }
                //WEEK
                2 -> {
                    dayWeekMonth = 2
                    perMonthMultiplie = 4
                    viewBinding.txtTimesPerMonth.hint = "Per week"
                    changeColor(viewBinding.btnDay.background, NOTSELECTEDCOLOR)
                    changeColor(viewBinding.btnWeek.background, SELECTEDCOLOR)
                    changeColor(viewBinding.btnMonth.background, NOTSELECTEDCOLOR)
                }
                //MONTH
                3 -> {
                    dayWeekMonth = 3
                    perMonthMultiplie = 1
                    viewBinding.txtTimesPerMonth.hint = "Per month"
                    changeColor(viewBinding.btnDay.background, NOTSELECTEDCOLOR)
                    changeColor(viewBinding.btnWeek.background, NOTSELECTEDCOLOR)
                    changeColor(viewBinding.btnMonth.background, SELECTEDCOLOR)
                }
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
                viewBinding.btnChooseActivty.background =
                    changeColor(viewBinding.btnChooseActivty.background, NOTSELECTEDCOLOR)
                viewBinding.btnchooseReward.background =
                    changeColor(viewBinding.btnchooseReward.background, SELECTEDCOLOR)
            }
            //Activity
            2 -> {
                isReward = false
                viewBinding.getPrioridad.adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    resources.getStringArray(R.array.Actividad)
                )
                viewBinding.btnChooseActivty.background =
                    changeColor(viewBinding.btnChooseActivty.background, SELECTEDCOLOR)
                viewBinding.btnchooseReward.background =
                    changeColor(viewBinding.btnchooseReward.background, NOTSELECTEDCOLOR)
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
            viewBinding.insertNombre.text.toString(),
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
            if (viewBinding.spinnerGetTag.selectedItemPosition != 0) viewBinding.spinnerGetTag.selectedItem.toString()
                .lowercase() else DEFAULTTAG,  //viewBinding.txtNombreTag.text.toString(), //"default", //temp
            now,
            0
        )
    }

    private fun checkIFok(obtained: CreateInformation): Boolean {
        var retorno = true

        val text = viewBinding.insertNombre.text.toString().lowercase()
        val lista = obtained.existingNames.toMutableList()
        lista.replaceAll(String::lowercase)
        if (obtained.isEdit)
            lista.remove(obtained.reward!!.name.lowercase())

        viewBinding.errorName.isVisible = lista.contains(text)
        if (text.trim().isBlank() or lista.contains(text)) {
            viewBinding.insertNombre.background =
                changeStroke(viewBinding.insertNombre.background, ERRORCOLOR)

            retorno = false
        } else viewBinding.insertNombre.background =
            changeStroke(viewBinding.insertNombre.background, CORRECTCOLOR)

        if (viewBinding.isLimited.isChecked && viewBinding.txtLimitedTimes.text.trim().isBlank()) {
            viewBinding.txtLimitedTimes.background =
                changeStroke(viewBinding.txtLimitedTimes.background, ERRORCOLOR)

            retorno = false
        } else viewBinding.txtLimitedTimes.background =
            changeStroke(viewBinding.txtLimitedTimes.background, CORRECTCOLOR)


        var timesPerMonth = -1
        try {
            timesPerMonth = viewBinding.txtTimesPerMonth.text.toString().toInt()
        } catch (nfe: NumberFormatException) {}
        val zeroException = timesPerMonth > 0
        if (viewBinding.txtTimesPerMonth.text.trim().isBlank() or !zeroException) {
            viewBinding.txtTimesPerMonth.background =
                changeStroke(viewBinding.txtTimesPerMonth.background, ERRORCOLOR)

            retorno = false
        } else viewBinding.txtTimesPerMonth.background =
            changeStroke(viewBinding.txtTimesPerMonth.background, CORRECTCOLOR)

        return retorno
    }

    private fun changeStroke(background: Drawable, color: String): Drawable {
        (background as? GradientDrawable)?.setStroke(if(color == TRANSPAREN)0 else 3, Color.parseColor(color))
        return background
    }

    private fun changeColor(backg: Drawable, color: String): Drawable {
        val drawable = DrawableCompat.wrap(backg)
        DrawableCompat.setTint(drawable, Color.parseColor(color))

        return drawable
    }

    private fun switchCreateTag(opt: Boolean) {
        popup = opt
        viewBinding.PopUpWindow.isVisible = popup

        viewBinding.insertNombre.isEnabled = !popup
        viewBinding.txtTimesPerMonth.isEnabled = !popup
        viewBinding.txtLimitedTimes.isEnabled = !popup
        viewBinding.spinnerGetTag.isEnabled = !popup
        viewBinding.getPrioridad.isEnabled = !popup
        viewBinding.isLimited.isEnabled = !popup


    }
}