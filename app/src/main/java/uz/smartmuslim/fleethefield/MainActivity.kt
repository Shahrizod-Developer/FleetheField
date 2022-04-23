package uz.smartmuslim.fleethefield

import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import uz.smartmuslim.fleethefield.model.SoundMenu
import uz.smartmuslim.fleethefield.utils.MySharedPreferences
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: MySharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MySharedPreferences.init(this)
        sharedPreferences = MySharedPreferences

        //   SoundMenu.create(this)

        if (sharedPreferences.sound == true) {
            SoundMenu.create(this)
        } else {
            SoundMenu.mediaPlayer.stop()
        }

        if (sharedPreferences.language == "eng") {
            setLocate("eng")
        } else {
            setLocate("ru")
        }
    }

    override fun onPause() {
        super.onPause()
        SoundMenu.mediaPlayer.pause()
    }

    private fun setLocate(language: String) {

        val resources: Resources = resources
        val metric: DisplayMetrics = resources.displayMetrics
        val configuration = resources.configuration
        configuration.locale = Locale(language)
        resources.updateConfiguration(configuration, metric)
        onConfigurationChanged(configuration)
        sharedPreferences.language = language
    }
}