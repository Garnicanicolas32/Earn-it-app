package com.example.rewardskotlin.firstTimeFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.rewardskotlin.dataAndClasses.MutableFragmentEnable
import com.example.rewardskotlin.databinding.FragmentTagsAndLimitedBinding


class tagsAndLimitedFragment : Fragment() {

    private lateinit var mutableFragmentEnable: MutableFragmentEnable

    private var _binding: FragmentTagsAndLimitedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTagsAndLimitedBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mutableFragmentEnable = ViewModelProvider(requireActivity()).get(MutableFragmentEnable::class.java)
        binding.btnGoLeft.setOnClickListener{
            mutableFragmentEnable.changePage(1)
        }

        binding.btnGoRight.setOnClickListener{
            mutableFragmentEnable.changePage(3)
        }
    }

}