package uz.smartmuslim.fleethefield.ui.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import uz.smartmuslim.fleethefield.R
import uz.smartmuslim.fleethefield.databinding.BonusScreenBinding

class BonusScreen : Fragment() {

    private lateinit var binding: BonusScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = BonusScreenBinding.inflate(layoutInflater, container, false)

        binding.levels.setOnClickListener {
            findNavController().navigate(R.id.action_bonusScreen_to_levelScreen)
        }

        return binding.root
    }
}