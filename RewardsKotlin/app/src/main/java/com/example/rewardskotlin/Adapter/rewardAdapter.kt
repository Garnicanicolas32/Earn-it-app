package com.example.rewardskotlin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rewardskotlin.dataAndClasses.Reward
import com.example.rewardskotlin.R

class rewardAdapter(private val listaRewards: List<Reward>, private val onClickListener:(Reward) -> Unit) : RecyclerView.Adapter<RewardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return RewardViewHolder(layoutInflater.inflate(R.layout.item_reward, parent, false))
    }

    override fun onBindViewHolder(holder: RewardViewHolder, position: Int) {
        val item = listaRewards[position]
        holder.render(item, onClickListener)
    }

    override fun getItemCount(): Int = listaRewards.size

}