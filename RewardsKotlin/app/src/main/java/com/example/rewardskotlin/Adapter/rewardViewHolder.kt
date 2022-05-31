package com.example.rewardskotlin.adapter

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.rewardskotlin.dataAndClasses.Reward
import com.example.rewardskotlin.dataAndClasses.OnClickReturn
import com.example.rewardskotlin.databinding.ItemRewardBinding

private val BUTTONCOLOR = listOf("#EF5350", "#EC407A", "#AB47BC","#7E57C2","#5C6BC0","#42A5F5")
private val POINTSCOLOR = listOf("#EF9A9A", "#F48FB1", "#CE93D8", "#B39DDB", "#9FA8DA", "#90CAF9")
private val TAGCOLOR = listOf("#FFCDD2", "#F8BBD0", "#E1BEE7", "#D1C4E9", "#C5CAE9", "#BBDEFB" )
// invisible color 00FFFFFF
private const val DEFAULTTAG = "default"

class RewardViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemRewardBinding.bind(view)
    fun render(first: Boolean, position: Int, useThis: Reward, onClickListener:(OnClickReturn) -> Unit){
        if(position >= 0 && position < BUTTONCOLOR.size){
            changeColor(binding.buttonSubmit.background, POINTSCOLOR[position])
            changeColor(binding.textView.background, BUTTONCOLOR[position])
            changeColor(binding.textView2.background, TAGCOLOR[position])
        }



        binding.buttonSubmit.text = useThis.price.toString()
        binding.textView.text = useThis.name
        binding.textView2.isVisible = first
        if(useThis.tagName == DEFAULTTAG && first)
            binding.textView2.visibility = View.INVISIBLE

        binding.textView2.text = useThis.tagName

        binding.buttonSubmit.setOnClickListener{
            onClickListener(OnClickReturn(useThis, false))
        }
        binding.textView.setOnClickListener{
            onClickListener(OnClickReturn(useThis, true))
        }
    }

    private fun changeColor(backg: Drawable, color: String): Drawable{
        val drawable = DrawableCompat.wrap(backg)
        DrawableCompat.setTint(drawable, Color.parseColor(color))
        return drawable
    }
}