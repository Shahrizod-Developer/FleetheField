package uz.smartmuslim.fleethefield.model

import android.content.Context
import android.media.MediaPlayer
import uz.smartmuslim.fleethefield.R

object SoundGame {

    var mediaPlayer = MediaPlayer()

    fun create(context: Context){
        mediaPlayer = MediaPlayer.create(context, R.raw.game)
        mediaPlayer.start()
    }
}