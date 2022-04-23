package uz.smartmuslim.fleethefield.ui.screen

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import uz.smartmuslim.fleethefield.R
import uz.smartmuslim.fleethefield.databinding.LevelScreenBinding
import uz.smartmuslim.fleethefield.utils.MySharedPreferences

class LevelScreen : Fragment() {


    private lateinit var binding: LevelScreenBinding
    private lateinit var sharedPreferences: MySharedPreferences

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = LevelScreenBinding.inflate(layoutInflater, container, false)

        MySharedPreferences.init(requireContext())
        sharedPreferences = MySharedPreferences
        when (sharedPreferences.levelCount) {
            in 0..1 -> {
                binding.one.setBackgroundColor(Color.RED)
            }
            2 -> {
                binding.two.setBackgroundColor(Color.RED)
            }
            3 -> {
                binding.two.setBackgroundColor(Color.RED)
                binding.three.setBackgroundColor(Color.RED)
            }
            else -> {
                binding.two.setBackgroundColor(Color.RED)
                binding.three.setBackgroundColor(Color.RED)
            }
        }
        binding.one.setOnClickListener {
            sharedPreferences.levelCount = 1
            findNavController().navigate(R.id.action_levelScreen_to_trustGameScreen)
        }
        binding.two.setOnClickListener {
            sharedPreferences.levelCount = 2
            findNavController().navigate(R.id.action_levelScreen_to_trustGameScreen)
        }
        binding.three.setOnClickListener {
            sharedPreferences.levelCount = 3
            findNavController().navigate(R.id.action_levelScreen_to_trustGameScreen)
        }
        binding.back.setOnClickListener {
            findNavController().navigate(R.id.action_levelScreen_to_mainScreen)
        }
        return binding.root
    }

}