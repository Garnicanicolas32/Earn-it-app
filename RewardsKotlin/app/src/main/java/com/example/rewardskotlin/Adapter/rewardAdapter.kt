package com.example.rewardskotlin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rewardskotlin.dataAndClasses.Reward
import com.example.rewardskotlin.R
import com.example.rewardskotlin.dataAndClasses.OnClickReturn

class RewardAdapter(private val listaRewards: List<Reward>, private val onClickListener:(OnClickReturn) -> Unit) : RecyclerView.Adapter<RewardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return RewardViewHolder(layoutInflater.inflate(R.layout.item_reward, parent, false))
    }

    override fun onBindViewHolder(holder: RewardViewHolder, position: Int) {
        var firstOne  = true
        if(position != 0)
            firstOne = listaRewards[position].tagName != listaRewards[position -1].tagName

        holder.render(firstOne,listaRewards[position], onClickListener)
    }

    override fun getItemCount(): Int = listaRewards.size

}