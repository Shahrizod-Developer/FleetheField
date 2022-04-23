package uz.smartmuslim.fleethefield.model

import android.content.Context
import android.media.MediaPlayer
import uz.smartmuslim.fleethefield.R

object SoundGameOver {

     var mediaPlayer = MediaPlayer()

    fun create(context: Context){
        mediaPlayer = MediaPlayer.create(context, R.raw.over)
    }
}