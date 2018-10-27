package studio.nodroid.bloodpressurehelper.model

data class PressureData(
    val systolic: Int,
    val diastolic: Int,
    val pulse: Int,
    val date: Date,
    val time: Time,
    val description: String = ""
)