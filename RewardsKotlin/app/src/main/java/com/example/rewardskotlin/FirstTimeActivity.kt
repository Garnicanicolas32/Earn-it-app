package com.example.rewardskotlin

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.rewardskotlin.adapter.ViewPageAdapter
import com.example.rewardskotlin.dataAndClasses.MutableFragmentEnable
import com.example.rewardskotlin.databinding.ActivityFirstTimeBinding
import com.example.rewardskotlin.firstTimeFragment.LplusRatioFragment
import com.example.rewardskotlin.firstTimeFragment.creatingRewardFragment
import com.example.rewardskotlin.firstTimeFragment.tagsAndLimitedFragment
import com.example.rewardskotlin.firstTimeFragment.welcomFragment

private const val KEY = "finishTutorial"

class FirstTimeActivity : AppCompatActivity() {

    private lateinit var mutableFragmentEnable: MutableFragmentEnable
    private lateinit var viewBinding: ActivityFirstTimeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityFirstTimeBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        mutableFragmentEnable = ViewModelProvider(this)[MutableFragmentEnable::class.java]



        val fragments: ArrayList<Fragment> = arrayListOf(
            welcomFragment(),
            creatingRewardFragment(),
            tagsAndLimitedFragment(),
            LplusRatioFragment()
        )


        val adapter = ViewPageAdapter(fragments, this)
        viewBinding.viewPager.adapter = adapter
        viewBinding.viewPager.isUserInputEnabled = false
        mutableFragmentEnable.currenPage.observe(this){
            MediaPlayer.create(this, R.raw.sound4).start()
            if(it in 0..3)
                Log.i("Excuse", "PASE BIEN")
            viewBinding.viewPager.currentItem = it
        }

        mutableFragmentEnable.currenBool.observe(this){
            if(it){
                mutableFragmentEnable.change(false)
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(KEY, mutableFragmentEnable.currenOption.value)
                startActivity(intent)
            }
        }
        viewBinding.viewPager.currentItem = 0
    }

    override fun onBackPressed() {
        if(viewBinding.viewPager.currentItem == 0)
            super.onBackPressed()
        else
            viewBinding.viewPager.currentItem = viewBinding.viewPager.currentItem - 1
    }
}