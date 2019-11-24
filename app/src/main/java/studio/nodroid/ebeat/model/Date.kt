package studio.nodroid.ebeat.model

data class Date(val year: Int, val month: Int, val day: Int) {
    override fun toString(): String = "$day.$month.$year"

    fun before(other: Date): Boolean {
        return (year < other.year)
                || (year == other.year && month < other.month)
                || (year == other.year && month == other.month && day < other.day)
    }
}