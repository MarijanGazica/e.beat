package studio.nodroid.bloodpressurehelper.model

data class Date(val year: Int, val month: Int, val day: Int) {
    override fun toString(): String = "$day.$month.$year"
}