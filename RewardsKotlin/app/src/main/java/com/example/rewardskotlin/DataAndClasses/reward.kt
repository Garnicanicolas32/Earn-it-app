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

data class Reward(
    var name: String,
    var price: Int,
    var basePrice: Float,
    //
    val isReward: Boolean,
    var isModify: Boolean,
    var isDelete: Boolean,
    //
    var isLimited: Boolean,
    var limitedTimes: Int,
    //
    var usagePercentageCount: Float,
    var usagePercentageADD: Float,
    //
    var prioridadMOD: Float,
    var timesPerMonthMOD: Float,
    var dayWeekMonthOption: Int,
    var timesPerX: Int,
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