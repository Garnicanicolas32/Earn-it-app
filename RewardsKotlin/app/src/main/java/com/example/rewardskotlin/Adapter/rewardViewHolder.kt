package com.example.rewardskotlin.adapter

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.rewardskotlin.R
import com.example.rewardskotlin.dataAndClasses.OnClickReturn
import com.example.rewardskotlin.dataAndClasses.Reward
import com.example.rewardskotlin.databinding.ItemRewardBinding


private val BUTTONCOLOR = listOf("#EF5350", "#EC407A", "#AB47BC", "#7E57C2", "#5C6BC0", "#42A5F5")
private val POINTSCOLOR = listOf("#EF9A9A", "#F48FB1", "#CE93D8", "#B39DDB", "#9FA8DA", "#90CAF9")
private val TAGCOLOR = listOf("#EF9A9A", "#F48FB1", "#CE93D8", "#B39DDB", "#9FA8DA", "#90CAF9")
private val BACKGROUNDCOLOR = listOf("#FFCDD2", "#F8BBD0", "#E1BEE7", "#D1C4E9", "#C5CAE9", "#BBDEFB")

// invisible color 00FFFFFF
private const val  DEFAULTTAG = "default"

class RewardViewHolder(view: View, ActPoints: Int) : RecyclerView.ViewHolder(view) {

    private val binding = ItemRewardBinding.bind(view)
    private val actualpoints = ActPoints

    private var taskSound: MediaPlayer? = MediaPlayer.create(view.context, R.raw.sound2)
    private var rewardSound: MediaPlayer? = MediaPlayer.create(view.context, R.raw.sound3)
    private var noPointsSound: MediaPlayer? = MediaPlayer.create(view.context, R.raw.sound1)

    fun render(
        first: Boolean,
        last: Boolean,
        position: Int,
        useThis: Reward,
        option: Int,
        onClickListener: (OnClickReturn) -> Unit
    ) {
        if (position >= 0 && position < BUTTONCOLOR.size) {
            changeColor(binding.btnPoints.background, POINTSCOLOR[position])
            changeColor(binding.btnDelete.background, POINTSCOLOR[position])
            changeColor(binding.btnEdit.background, POINTSCOLOR[position])

            changeColor(binding.txtUsos.background, POINTSCOLOR[position])

            changeColor(binding.txtName.background, BUTTONCOLOR[position])
            changeColor(binding.txtTag.background, TAGCOLOR[position])

            changeColor(binding.backBoth.background, BACKGROUNDCOLOR[position])
            changeColor(binding.backLast.background, BACKGROUNDCOLOR[position])
            changeColor(binding.backFirst.background, BACKGROUNDCOLOR[position])
            changeColor(binding.backMiddle.background, BACKGROUNDCOLOR[position])
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

        //tag & line
        binding.txtTag.isVisible = first
        if(useThis.tagName == DEFAULTTAG) {
            binding.txtTag.visibility = View.INVISIBLE
        }
        binding.txtTag.text = useThis.tagName

        if(!(last || first)){
            val params = binding.txtName.layoutParams as ConstraintLayout.LayoutParams
            params.topToTop = binding.backMiddle.id
            params.bottomToBottom = binding.backMiddle.id
            binding.txtName.requestLayout()
        }

        if(last && !first){
            val params = binding.txtName.layoutParams as ConstraintLayout.LayoutParams
            params.topToTop = binding.backLast.id
            params.bottomToBottom = binding.backLast.id
            binding.txtName.requestLayout()
        }

        //Background
        binding.spaceTop.isVisible = first // || useThis.tagName == DEFAULTTAG
        binding.spaceTag.isVisible = first

        binding.spaceBot.isVisible = last && !first
        binding.spaceBotBoth.isVisible = first && last

        binding.backBoth.isVisible = first && last
        binding.backFirst.isVisible = first && !last
        binding.backLast.isVisible = last && !first
        binding.backMiddle.isVisible = !(last || first)

        //limited times
        binding.txtUsos.isVisible = useThis.limitedTimes > 0 //REMOVE THIS
        if (useThis.limitedTimes > 0)
            binding.txtUsos.text = useThis.limitedTimes.toString() + " uses"

        binding.btnPoints.setOnClickListener {
            if(useThis.price < 0 && actualpoints + useThis.price >= 0){
            binding.btnPoints.animate().apply {
                rewardSound?.start()
                duration = 300
                translationYBy(-30f)
                binding.btnPoints.setTextColor(Color.parseColor("#DEF6DA"))
            }.withEndAction{
                binding.btnPoints.animate().apply {
                    duration = 300
                    translationYBy(30f)
                }
            }.withEndAction{
                onClickListener(OnClickReturn(useThis, false, isDelete = false))
            }.start()}
            else if(useThis.price > 0){
                binding.btnPoints.animate().apply {
                    taskSound?.start()
                    duration = 100
                    scaleXBy(0.1f)
                    scaleYBy(0.1f)
                    binding.btnPoints.setTextColor(Color.parseColor("#DEF6DA"))
                }.withEndAction{
                    onClickListener(OnClickReturn(useThis, false, isDelete = false))
                }
            }
            if(useThis.price < 0 && actualpoints + useThis.price < 0){
                binding.btnPoints.animate().apply {
                    noPointsSound?.start()
                    binding.btnPoints.setTextColor(Color.parseColor("#d43149"))
                    duration = 300
                    YoYo.with(Techniques.RubberBand).duration(300).repeat(1).playOn(binding.btnPoints)
                }.withEndAction{
                    onClickListener(OnClickReturn(useThis, false, isDelete = false))
                }.start()
            }
        }

        binding.btnEdit.setOnClickListener {
            onClickListener(OnClickReturn(useThis, true, isDelete = false))
        }
        binding.btnEdit.setOnClickListener {
            onClickListener(OnClickReturn(useThis, true, isDelete = false))
        }
        binding.btnDelete.setOnClickListener {
            onClickListener(OnClickReturn(useThis, false, isDelete = true))
        }
    }

    private fun changeColor(backg: Drawable, color: String): Drawable {
        val drawable = DrawableCompat.wrap(backg)
        DrawableCompat.setTint(drawable, Color.parseColor(color))
        return drawable
    }
}