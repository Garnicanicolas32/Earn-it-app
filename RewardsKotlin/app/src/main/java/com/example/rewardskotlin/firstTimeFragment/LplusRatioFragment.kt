package com.example.rewardskotlin.firstTimeFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.example.rewardskotlin.R
import com.example.rewardskotlin.databinding.FragmentLplusRatioBinding

class LplusRatioFragment : Fragment() {

    private var _binding:FragmentLplusRatioBinding? = null
    private val binding get() = _binding!!
    private var give = 2
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLplusRatioBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //binding.seekBar
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onStopTrackingTouch(p0: SeekBar?) {
                if (p0 != null){
                    change(p0.progress)
                    give = p0.progress
                }
            }

            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
        })

    }

    private fun change(num: Int){
        when(num){
            0->{ binding.imageView6.setImageResource(R.drawable.img_balance_one) }
            1->{ binding.imageView6.setImageResource(R.drawable.img_balance_two) }
            2->{ binding.imageView6.setImageResource(R.drawable.img_balance_three) }
            3->{ binding.imageView6.setImageResource(R.drawable.img_balance_four) }
            4->{ binding.imageView6.setImageResource(R.drawable.img_balance_five) }
            else->{ binding.imageView6.setImageResource(R.drawable.img_balance_three) }
        }
    }
}