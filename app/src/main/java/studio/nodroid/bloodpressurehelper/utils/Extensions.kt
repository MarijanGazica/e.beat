package studio.nodroid.bloodpressurehelper.utils

import java.math.BigDecimal
import java.math.RoundingMode

fun Double.roundTo(places: Int): Double {
    if (places < 0) throw IllegalArgumentException()

    var bd = BigDecimal(this)
    bd = bd.setScale(places, RoundingMode.HALF_UP)
    return bd.toDouble()
}