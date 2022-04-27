package com.example.rewardskotlin

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rewardskotlin.Adapter.rewardAdapter
import com.example.rewardskotlin.DataAndClasses.DataReward
import com.example.rewardskotlin.DataAndClasses.RewardsActuales
import com.example.rewardskotlin.DataAndClasses.SaveFormat
import com.example.rewardskotlin.DataAndClasses.reward
import com.example.rewardskotlin.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    // -V- KEYS -V-
    private val SHARED = "Data"
    private val KEY = "json"
    private val FIRSTIME = "First"

    // -V- global vars -V-
    private lateinit var preferences: SharedPreferences
    private lateinit var accesoView: ActivityMainBinding
    private lateinit var datosGlobales: DataReward
    private var data = SaveFormat(
        RewardsActuales.listaVacia,
        RewardsActuales.listaVacia,
        RewardsActuales.listaVacia,
        0,
        1
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accesoView = ActivityMainBinding.inflate(layoutInflater)
        setContentView(accesoView.root)
        datosGlobales = ViewModelProvider(this).get(DataReward::class.java)

        preferences = this.getSharedPreferences(SHARED, 0)
        if (preferences.getBoolean(FIRSTIME, true)) {
            //first time
            preferences.edit().putBoolean(FIRSTIME, false).apply()
        }

        data = Load()

        accesoView.TxtPuntos.text = data.puntos.toString()
        datosGlobales.changePoints(data.puntos)

        // -V- points change -V-
        datosGlobales.currentPoints.observe(this, Observer {
            accesoView.TxtPuntos.text = it.toString()
        })

        // -V- Recycle View -V-
        initRecyclerView(data.listaA)
        // -V- Change list-V-
        accesoView.button.setOnClickListener {
            initRecyclerView(data.listaA)
        }

        accesoView.button2.setOnClickListener {
            initRecyclerView(data.listaB)
        }

        //Change view
        accesoView.btnCreateReward.setOnClickListener {
            val intent = Intent(this, createReward::class.java)
            startActivity(intent)
        }

        //NEW OBJECT OBTAINED
        if (!intent.getStringExtra("NewReward").isNullOrBlank()) {
            //NEW OBJECT YEY
            val objeto =
                Gson().fromJson(intent.getStringExtra("NewReward") + "", reward::class.java)
            data.listaA = data.listaA + objeto
            Save(data)
            refresh()
        }
    }

    //Recycler View
    private fun initRecyclerView(lista: List<reward>) {
        accesoView.listaViewRewards.layoutManager = LinearLayoutManager(this)
        accesoView.listaViewRewards.adapter = rewardAdapter(lista) {
            onItemSelected(
                it
            )
        }
    }

    private fun onItemSelected(objectiveReward: reward) {
        // -V- apreto boton -V-
        if (objectiveReward.modificar)
            Toast.makeText(this, "Modificar " + objectiveReward.nombre, Toast.LENGTH_SHORT).show()
        else if (!datosGlobales.changePoints(objectiveReward.precioActual))
            Toast.makeText(this, "Puntos Insuficientes", Toast.LENGTH_SHORT).show()
    }

    private fun refresh() {
        initRecyclerView(data.listaA)
    }
    //DATA

    private fun Save(saveFormat: SaveFormat) {
        preferences.edit().putString(KEY, Gson().toJson(saveFormat)).apply()
    }

    private fun Load(): SaveFormat {

        //first blank
        var saveFormat = SaveFormat(
            RewardsActuales.listaVacia,
            RewardsActuales.listaVacia,
            RewardsActuales.listaVacia,
            0,
            1
        )
        //check if exist
        val str: String? = preferences.getString(KEY, "")
        if (!str.isNullOrBlank())
            saveFormat = Gson().fromJson(str, SaveFormat::class.java)

        return saveFormat
    }
}