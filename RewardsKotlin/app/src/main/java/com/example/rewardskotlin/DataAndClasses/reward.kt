package com.example.rewardskotlin.DataAndClasses

data class reward(
    var nombre: String,
    var precioActual: Int,
    //
    val isReward: Boolean,
    var modificar: Boolean,
    //
    var limited: Boolean,
    var TimesLeft: Int,
    //
    var UsagePercentageCount: Float,
    var UsagePercentageAdd: Float,
    //
    var Prioridad_TiempoGastaMOD: Float,
    var TimesPerMonthMOD: Float,
    //
    var DiscountMOD: Float,
    var DiscountTimeLeft: Int,
    //
    var Tag: String
)

data class SaveFormat(
    var listaA: List<reward>,
    var listaB: List<reward>,
    var listaC: List<reward>,
    val puntos: Int,
    val RewardRatio: Int
)