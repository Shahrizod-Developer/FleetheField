package uz.smartmuslim.fleethefield.utils

import android.content.Context
import android.content.SharedPreferences

object MySharedPreferences {

    private const val NAME = "app_name"
    private const val MODE = Context.MODE_PRIVATE
    lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor: SharedPreferences.Editor = edit()
        operation(editor)
        editor.apply()
    }

    var language: String?
        get() = preferences.getString("language", "")
        set(value) = preferences.edit()
        {
            if (value != null) {
                it.putString("language", value)
            }
        }

    var bonusCount: Int?
        get() = preferences.getInt("bonusCount", 0)
        set(value) = preferences.edit()
        {
            if (value != null) {
                it.putInt("bonusCount", value)
            }
        }
    var levelCount: Int?
        get() = preferences.getInt("levelCount", 0)
        set(value) = preferences.edit()
        {
            if (value != null) {
                it.putInt("levelCount", value)
            }
        }
    var time: Int?
        get() = preferences.getInt("time", 0)
        set(value) = preferences.edit()
        {
            if (value != null) {
                it.putInt("time", value)
            }
        }
    var sound: Boolean?
        get() = preferences.getBoolean("sound", false)
        set(value) = preferences.edit()
        {
            if (value != null) {
                it.putBoolean("sound", value)
            }
        }
    var bonus: Boolean?
        get() = preferences.getBoolean("bunus", false)
        set(value) = preferences.edit()
        {
            if (value != null) {
                it.putBoolean("bunus", value)
            }
        }

    var vibration: Boolean?
        get() = preferences.getBoolean("vibration", false)
        set(value) = preferences.edit()
        {
            if (value != null) {
                it.putBoolean("vibration", value)
            }
        }
    var gameOver: Boolean?
        get() = preferences.getBoolean("gameOver", false)
        set(value) = preferences.edit()
        {
            if (value != null) {
                it.putBoolean("gameOver", value)
            }
        }
}