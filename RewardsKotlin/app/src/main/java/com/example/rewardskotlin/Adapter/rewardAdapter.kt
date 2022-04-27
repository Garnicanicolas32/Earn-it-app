package com.example.rewardskotlin.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rewardskotlin.DataAndClasses.reward
import com.example.rewardskotlin.R

class rewardAdapter(private val listaRewards: List<reward>, private val onClickListener:(reward) -> Unit) : RecyclerView.Adapter<rewardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): rewardViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return rewardViewHolder(layoutInflater.inflate(R.layout.item_reward, parent, false))
    }

    override fun onBindViewHolder(holder: rewardViewHolder, position: Int) {
        val item = listaRewards[position]
        holder.render(item, onClickListener)
    }

    override fun getItemCount(): Int = listaRewards.size

}