package uz.smartmuslim.fleethefield.ui.screen

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import uz.smartmuslim.fleethefield.R
import uz.smartmuslim.fleethefield.databinding.SettingsScreenBinding
import uz.smartmuslim.fleethefield.model.SoundGame
import uz.smartmuslim.fleethefield.model.SoundGameOver
import uz.smartmuslim.fleethefield.model.SoundMenu
import uz.smartmuslim.fleethefield.model.Vibration
import uz.smartmuslim.fleethefield.ui.screen.game.TrustGameScreen
import uz.smartmuslim.fleethefield.utils.MySharedPreferences
import java.util.*

class SettingsScreen : Fragment() {

    private lateinit var binding: SettingsScreenBinding
    private lateinit var mySharedPreferences: MySharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = SettingsScreenBinding.inflate(layoutInflater, container, false)

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
        MySharedPreferences.init(requireContext())
        mySharedPreferences = MySharedPreferences

        binding.switchSound.isChecked = SoundMenu.mediaPlayer.isPlaying
        binding.switchVibration.isChecked = Vibration.vibrate

        binding.switchSound.setOnCheckedChangeListener { compoundButton, ischecked ->

            if (ischecked) {
                SoundMenu.create(requireContext())
                TrustGameScreen().context?.let { SoundGame.create(it) }
                TrustGameScreen().context?.let { SoundGameOver.create(it) }
                mySharedPreferences.sound = true
                mySharedPreferences.vibration = true
                mySharedPreferences.gameOver = true
            } else {
                SoundMenu.mediaPlayer.stop()
                SoundGame.mediaPlayer.stop()
                SoundGameOver.mediaPlayer.stop()
                mySharedPreferences.sound = false
                mySharedPreferences.vibration = false
                mySharedPreferences.gameOver = false
            }
        }
        binding.switchSound.isChecked = mySharedPreferences.sound!!

        binding.switchVibration.setOnCheckedChangeListener { compoundButton, b ->
            Vibration.vibrate = b

            if (b) {
                Vibration.vibrate = true
                mySharedPreferences.vibration = true
            } else {
                Vibration.vibrate = false
                mySharedPreferences.vibration = false
            }
        }
        binding.switchVibration.isChecked = mySharedPreferences.vibration!!

        binding.eng.setOnClickListener {
            val language = "eng"
            setLocate(language)
        }
        binding.ru.setOnClickListener {
            val language = "ru"
            setLocate(language)
        }

        return binding.root
    }

    private fun setLocate(language: String) {

        val resources: Resources = resources
        val metric: DisplayMetrics = resources.displayMetrics
        val configuration = resources.configuration
        configuration.locale = Locale(language)
        resources.updateConfiguration(configuration, metric)
        onConfigurationChanged(configuration)
        mySharedPreferences.language = language
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        binding.textSound.text = requireContext().resources.getString(R.string.sound)
        binding.textVibration.text = requireContext().resources.getString(R.string.vibration)
        binding.textLanguage.text = requireContext().resources.getString(R.string.language)
    }
}