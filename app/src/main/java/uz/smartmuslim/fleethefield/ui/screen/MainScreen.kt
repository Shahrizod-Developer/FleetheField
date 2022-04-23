package uz.smartmuslim.fleethefield.ui.screen

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import uz.smartmuslim.fleethefield.R
import uz.smartmuslim.fleethefield.databinding.MainScreenBinding
import uz.smartmuslim.fleethefield.model.SoundMenu
import uz.smartmuslim.fleethefield.utils.MySharedPreferences
import java.util.*


class MainScreen : Fragment() {

    private lateinit var binding: MainScreenBinding
    private lateinit var sharedPreferences: MySharedPreferences
    private val START_TIME_IN_MILLIS: Long = 86400000
    private var mediaPlayer: MediaPlayer? = null
    private var mCountDownTimer: CountDownTimer? = null
    private var mTimerRunning = false
    private var mTimeLeftInMillis: Long = 0
    private var mEndTime: Long = 0
    private var remainingTimeInMillis: Long = 0
    var count = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = MainScreenBinding.inflate(layoutInflater, container, false)

        MySharedPreferences.init(requireContext())
        sharedPreferences = MySharedPreferences

        //     SoundMenu.create(requireContext())

//        if (sharedPreferences.sound == true)
//        {
//            SoundMenu.create(requireContext())
//        }
//        else{
//            SoundMenu.mediaPlayer.stop()
//        }

        count = sharedPreferences.bonusCount!!
        binding.settings.setOnClickListener {
            findNavController().navigate(R.id.action_mainScreen_to_settingsScreen)
        }
        binding.start.setOnClickListener {
            findNavController().navigate(R.id.action_mainScreen_to_trustGameScreen)
        }

        binding.getBonus.setOnClickListener {
            count++
            findNavController().navigate(R.id.action_mainScreen_to_bonusScreen)
            sharedPreferences.bonus = true
            sharedPreferences.bonusCount = count

        }
        binding.levels.setOnClickListener {
            findNavController().navigate(R.id.action_mainScreen_to_levelScreen)
        }

        return binding.root
    }


    override fun onPause() {
        super.onPause()
        SoundMenu.mediaPlayer.pause()
    }

}