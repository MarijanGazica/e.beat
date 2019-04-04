package studio.nodroid.ebeat.model

data class Time(val hour: Int, val minute: Int) {
    override fun toString(): String = "${hour.timeDisplayValue()}:${minute.timeDisplayValue()}"
}

fun Int.timeDisplayValue(): String = this.toString().padStart(2, '0')