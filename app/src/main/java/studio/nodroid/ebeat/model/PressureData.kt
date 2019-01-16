package studio.nodroid.ebeat.model

data class PressureData(
    val systolic: Int,
    val diastolic: Int,
    val pulse: Int,
    val date: Date,
    val time: Time,
    val timestamp: Long,
    val description: String = ""
)