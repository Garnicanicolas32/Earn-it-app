package com.example.rewardskotlin.DataAndClasses

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.function.BinaryOperator

class DataReward : ViewModel() {

    var points: Int = 0;
    val currentPoints: MutableLiveData<Int> by lazy{
        MutableLiveData<Int>()
    }

    public fun changePoints(cantidad: Int): Boolean{
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