package com.example.rewardskotlin.dataAndClasses

data class Reward(
    var name: String,
    var price: Int,
    var basePrice: Float,
    //
    val isReward: Boolean,
    var isModify: Boolean,
    //
    var isLimited: Boolean,
    var TimesLeft: Int,
    //
    var usagePercentageCount: Float,
    var usagePercentageADD: Float,
    //
    var prioridadMOD: Float,
    var timesPerMonthMOD: Float,
    //
    var discountMOD: Float,
    var discountTimeLeft: Int,
    //
    var tagName: String
)

data class SaveFormat(
    var listRewards: List<Reward>,
    var listActivities: List<Reward>,
    var globalPoints: Int,
    var rewardRatio: Float
)