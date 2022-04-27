package com.example.rewardskotlin.Adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.rewardskotlin.DataAndClasses.reward
import com.example.rewardskotlin.databinding.ItemRewardBinding

class rewardViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemRewardBinding.bind(view)
    fun render(reward: reward, onClickListener:(reward) -> Unit){
        binding.buttonSubmit.text = reward.precioActual.toString()
        binding.textView.text = reward.nombre
        binding.buttonSubmit.setOnClickListener{
            onClickListener(reward)
        }
        binding.textView.setOnClickListener{
            reward.modificar = true
            onClickListener(reward)
            reward.modificar = false
        }
    }
}