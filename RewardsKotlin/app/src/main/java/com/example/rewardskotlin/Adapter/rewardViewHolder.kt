package com.example.rewardskotlin.adapter

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.rewardskotlin.dataAndClasses.OnClickReturn
import com.example.rewardskotlin.dataAndClasses.Reward
import com.example.rewardskotlin.databinding.ItemRewardBinding

private val BUTTONCOLOR = listOf("#EF5350", "#EC407A", "#AB47BC","#7E57C2","#5C6BC0","#42A5F5")
private val POINTSCOLOR = listOf("#EF9A9A", "#F48FB1", "#CE93D8", "#B39DDB", "#9FA8DA", "#90CAF9")
private val TAGCOLOR = listOf("#FFCDD2", "#F8BBD0", "#E1BEE7", "#D1C4E9", "#C5CAE9", "#BBDEFB" )
// invisible color 00FFFFFF
private const val DEFAULTTAG = "default"

class RewardViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemRewardBinding.bind(view)
    fun render(first: Boolean, position: Int, useThis: Reward, option: Int, onClickListener:(OnClickReturn) -> Unit){
        if(position >= 0 && position < BUTTONCOLOR.size){
            changeColor(binding.btnPoints.background, POINTSCOLOR[position])
            changeColor(binding.btnDelete.background, POINTSCOLOR[position])
            changeColor(binding.btnEdit.background, POINTSCOLOR[position])

            changeColor(binding.txtName.background, BUTTONCOLOR[position])
            changeColor(binding.txtTag.background, TAGCOLOR[position])
        }

        //Option
        when(option){
            0->{
                binding.btnPoints.isVisible = true
                binding.btnDelete.isVisible = false
                binding.btnEdit.isVisible = false
            }
            1->{
                binding.btnPoints.isVisible = false
                binding.btnDelete.isVisible = true
                binding.btnEdit.isVisible = false
            }
            2->{
                binding.btnPoints.isVisible = false
                binding.btnDelete.isVisible = false
                binding.btnEdit.isVisible = true
            }
            else->{
                binding.btnPoints.isVisible = true
                binding.btnDelete.isVisible = false
                binding.btnEdit.isVisible = false
            }
        }

        //General
        binding.btnPoints.text = useThis.price.toString()
        binding.txtName.text = useThis.name

        //tag
        binding.txtTag.isVisible = first
        if(useThis.tagName == DEFAULTTAG && first)
            binding.txtTag.visibility = View.INVISIBLE
        binding.txtTag.text = useThis.tagName

        //limited times
        binding.txtUsos.isVisible = useThis.limitedTimes > 0
        if(useThis.limitedTimes > 0)
        binding.txtUsos.text = HtmlCompat.fromHtml(useThis.limitedTimes.toString() + " <br> usos", HtmlCompat.FROM_HTML_MODE_LEGACY).toString()

        binding.btnPoints.setOnClickListener{
            onClickListener(OnClickReturn(useThis, false, isDelete = false))
        }
        binding.btnEdit.setOnClickListener{
            onClickListener(OnClickReturn(useThis, true, isDelete = false))
        }
        binding.btnEdit.setOnClickListener{
            onClickListener(OnClickReturn(useThis, true, isDelete = false))
        }
        binding.btnDelete.setOnClickListener(){
            onClickListener(OnClickReturn(useThis,false, isDelete = true))
        }

        /*binding.txtName.setOnClickListener{
            onClickListener(OnClickReturn(useThis, true, false))
        }*/
    }

    private fun changeColor(backg: Drawable, color: String): Drawable{
        val drawable = DrawableCompat.wrap(backg)
        DrawableCompat.setTint(drawable, Color.parseColor(color))
        return drawable
    }
}