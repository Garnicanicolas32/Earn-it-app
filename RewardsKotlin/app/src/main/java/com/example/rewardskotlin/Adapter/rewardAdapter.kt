package com.example.rewardskotlin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rewardskotlin.dataAndClasses.Reward
import com.example.rewardskotlin.R
import com.example.rewardskotlin.dataAndClasses.OnClickReturn

class RewardAdapter(private val listaRewards: List<Reward>, private val listTags: List<String>,private val option: Int , private val points: Int, private val onClickListener:(OnClickReturn) -> Unit) : RecyclerView.Adapter<RewardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return RewardViewHolder(layoutInflater.inflate(R.layout.item_reward, parent, false), points)
    }

    override fun onBindViewHolder(holder: RewardViewHolder, position: Int) {
        //Is first in tag
        val firstOne =
        if(position != 0)
            listaRewards[position].tagName != listaRewards[position -1].tagName
        else
            true
        //Is last in tag
        val lastOne =
        if(position < listaRewards.size - 1)
            listaRewards[position].tagName != listaRewards[position + 1].tagName
        else
            true
        //TagPosition
        val tagPosition = listTags.indexOf(listaRewards[position].tagName)
        holder.render(firstOne,lastOne,tagPosition, listaRewards[position], option, onClickListener)
    }

    override fun getItemCount(): Int = listaRewards.size

}