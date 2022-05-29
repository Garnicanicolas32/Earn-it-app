package com.example.rewardskotlin.dataAndClasses

import java.time.LocalDateTime

class MyOwnClock(localDateTime: LocalDateTime) {
    private var year: Int = localDateTime.year
    private var month: Int = localDateTime.monthValue
    private var day: Int = localDateTime.dayOfMonth
    private var hour: Int = localDateTime.hour
    private var minute: Int = localDateTime.minute

    fun devolver(): LocalDateTime{
        return LocalDateTime.of(this.year,this.month,this.day,this.hour,this.minute)
    }
}

data class Reward(
    var name: String,
    var price: Int,
    var basePrice: Float,
    var limitedTimes: Int,
    var usagePercentageCount: Float,
    var options: List<Int>, //first daymonthyear second xtimes third prioridad
    var discountMOD: Float,
    var discountRemoveAfter: MyOwnClock,
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

data class CreateInformation(
    var isEdit: Boolean,
    var tags: List<String>,
    var existingNames: List<String>,
    var reward: Reward?
)

data class SendBack(
    var isEdit: Boolean,
    var isDelete: Boolean,
    var reward: Reward,
    var oldOne: Reward?
)

data class OnClickReturn(
    val reward: Reward,
    val isEdit: Boolean
)