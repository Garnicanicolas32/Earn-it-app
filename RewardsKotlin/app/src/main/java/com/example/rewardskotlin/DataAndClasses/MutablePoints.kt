package com.example.rewardskotlin.dataAndClasses

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MutablePoints : ViewModel() {

    private var points: Int = 0

    val currentPoints: MutableLiveData<Int> by lazy{
        MutableLiveData<Int>()
    }

    fun changePoints(cantidad: Int): Boolean{
        var passOk = true
        points += cantidad
        if(points < 0) {
            points -= cantidad
            passOk = false
        }
        currentPoints.value = points
        return passOk
    }
}