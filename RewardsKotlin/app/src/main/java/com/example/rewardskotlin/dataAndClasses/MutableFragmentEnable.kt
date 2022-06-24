package com.example.rewardskotlin.dataAndClasses

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MutableFragmentEnable : ViewModel() {

    private var option: Int = 2
    private var enable: Boolean = true
    private var page: Int = 0

    val currenOption: MutableLiveData<Int> by lazy{
        MutableLiveData<Int>()
    }
    val currenBool: MutableLiveData<Boolean> by lazy{
        MutableLiveData<Boolean>()
    }
    val currenPage: MutableLiveData<Int> by lazy{
        MutableLiveData<Int>()
    }

    fun changePage(num: Int){
        page = num
        currenPage.value = page
    }

    fun changeRatio(num: Int){
        option = num
        currenOption.value = option
    }
    fun change(ret: Boolean){
        enable = ret
        currenBool.value = enable
    }
}