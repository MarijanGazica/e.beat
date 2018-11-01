package studio.nodroid.bloodpressurehelper.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PressureDataDB(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val systolic: Int,
    val diastolic: Int,
    val pulse: Int,
    val timestamp: Long,
    val description: String?,
    val userId: Int,
    val weight: Int
)