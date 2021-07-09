package studio.nodroid.ebeat.time

class TimeProvider {

    fun getCurrentTime(): Long {
        return System.currentTimeMillis()
    }
}