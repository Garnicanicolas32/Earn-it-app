package com.example.rewardskotlin.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.rewardskotlin.dataAndClasses.Reward
import com.example.rewardskotlin.dataAndClasses.OnClickReturn
import com.example.rewardskotlin.databinding.ItemRewardBinding

class RewardViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemRewardBinding.bind(view)
    fun render(useThis: Reward, onClickListener:(OnClickReturn) -> Unit){
        binding.buttonSubmit.text = useThis.price.toString()
        binding.textView.text = useThis.name

        binding.buttonSubmit.setOnClickListener{
            onClickListener(OnClickReturn(useThis, false))
        }
        binding.textView.setOnClickListener{
            onClickListener(OnClickReturn(useThis, true))
        }
    }
}