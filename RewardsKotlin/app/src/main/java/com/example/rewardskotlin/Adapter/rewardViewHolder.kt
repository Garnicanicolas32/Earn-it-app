package com.example.rewardskotlin.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.rewardskotlin.dataAndClasses.Reward
import com.example.rewardskotlin.databinding.ItemRewardBinding

class RewardViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemRewardBinding.bind(view)
    fun render(useThis: Reward, onClickListener:(Reward) -> Unit){
        binding.buttonSubmit.text = useThis.price.toString()
        binding.textView.text = useThis.name
        binding.buttonSubmit.setOnClickListener{
            onClickListener(useThis)
        }
        binding.textView.setOnClickListener{
            useThis.isModify = true
            onClickListener(useThis)
            useThis.isModify = false
        }
    }
}