package uz.smartmuslim.fleethefield.utils

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import java.util.concurrent.TimeUnit


class TimerService : Service() {

    private val finishedIntent = Intent(ACTION_FINISHED)

    private val tickIntent = Intent(ACTION_TICK)

    private lateinit var timer: CountDownTimer

    override fun onCreate() {
        timer = createTimer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        timer.start()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        timer.cancel()
    }

    private fun createTimer(): CountDownTimer =
        object : CountDownTimer(COUNTDOWN_LENGTH, COUNTDOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                tickIntent.putExtra(TIME_LEFT_KEY, millisUntilFinished)
                sendBroadcast(tickIntent)
            }

            override fun onFinish() {
                sendBroadcast(finishedIntent)
                stopSelf() // Stop the service within itself NOT the activity
            }
        }

    companion object {

        const val ACTION_FINISHED: String = "your.pkg.name.ACTION_FINISHED"

        const val ACTION_TICK: String = "your.pkg.name.ACTION_TICK"

        const val TIME_LEFT_KEY: String = "timeLeft"

        private val COUNTDOWN_INTERVAL = TimeUnit.SECONDS.toMillis(1)

        private val COUNTDOWN_LENGTH = TimeUnit.MINUTES.toMillis(5)
    }
}