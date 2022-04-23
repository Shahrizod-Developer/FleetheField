package uz.smartmuslim.fleethefield.model

import android.graphics.Point
import android.graphics.drawable.Drawable

data class Item(
    var position: Point?,
    var image: Int,
    var color: Int?,
    var type: Type
)

enum class Type {
    Person,
    Stone,
    Bot,
    Flag,
    Empty,
    None,
    Exit
}
