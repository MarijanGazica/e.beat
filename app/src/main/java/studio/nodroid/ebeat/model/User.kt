package studio.nodroid.ebeat.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)