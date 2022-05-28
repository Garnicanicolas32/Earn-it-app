package com.example.rewardskotlin.dataAndClasses

import java.time.LocalDateTime
import java.util.*

class MyOwnClock(localDateTime: LocalDateTime) {
    var year: Int = localDateTime.year
    var month: Int = localDateTime.monthValue
    var day: Int = localDateTime.dayOfMonth
    var hour: Int = localDateTime.hour
    var minute: Int = localDateTime.minute

    fun devolver(): LocalDateTime{
        return LocalDateTime.of(this.year,this.month,this.day,this.hour,this.minute)
    }
}

data class Reward(  // Find a way to change ISMODIFY or ISDELETE TODO
    var name: String,
    var price: Int,
    var basePrice: Float,
    //
    //var isModify: Boolean,
    //var isDelete: Boolean,
    //
    var limitedTimes: Int,
    //
    var usagePercentageCount: Float,
    //
    var options: List<Int>, //first daymonthyear second xtimes third prioridad
    //
    var discountMOD: Float,
    var discountRemoveAfter: MyOwnClock,
    //
    var tagName: String,
    //
    var lastTimeUsed: MyOwnClock,
    var timesRemoved: Int
)

data class SaveFormat(
    var listRewards: List<Reward>,
    var listActivities: List<Reward>,
    var globalPoints: Int,
    var rewardRatio: Float
)

data class createInformation(
    var isEdit: Boolean,
    var tags: List<String>,
    var reward: Reward?
)

data class sendBack(
    var isEdit: Boolean,
    var isDelete: Boolean,
    var reward: Reward,
    var oldOne: Reward?
)

data class onClickReturn(
    val reward: Reward,
    val isEdit: Boolean
)
/*
if (obtained.isDelete) {
                deleteReward(obtained.reward)
            } else {
                if (intent.getBooleanExtra("isChangeAndDelete", false)) {
                    val oldReward =
                        Gson().fromJson(intent.getStringExtra("OldObject") + "", Reward::class.java)
                    deleteReward(oldReward)
                }
                if (obtained.basePrice < 0) {
                    globalData.listRewards = globalData.listRewards + obtained
                    rewardSelected = true
                } else {
                    globalData.listActivities = globalData.listActivities + obtained
                    rewardSelected = false
                }
            }


intent.putExtra("NewReward", false) //IF SOMETHING ISN'T WORKING CHECK IF IT ISN'T THIS

        private fun sendAndGo(json: String, json2: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("NewReward", json)
        intent.putExtra("isChangeAndDelete", true)
        intent.putExtra("OldObject", json2)
        startActivity(intent)
    }
* */