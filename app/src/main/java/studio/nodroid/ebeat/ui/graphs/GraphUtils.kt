package studio.nodroid.ebeat.ui.graphs

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.LargeValueFormatter
import studio.nodroid.ebeat.R

fun generateSystolicGraphLine(context: Context): LineDataSet {
    val set = LineDataSet(mutableListOf(), context.resources.getString(R.string.systolic))
    set.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
    set.color = ResourcesCompat.getColor(context.resources, R.color.severity_red, context.theme)
    set.setCircleColor(ResourcesCompat.getColor(context.resources, R.color.severity_red, context.theme))
    set.lineWidth = 2f
    set.isHighlightEnabled = false
    set.valueFormatter = LargeValueFormatter()
    return set
}

fun generateDiastolicGraphLine(context: Context): LineDataSet {
    val set = LineDataSet(mutableListOf(), context.resources.getString(R.string.diastolic))
    set.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
    set.color = ResourcesCompat.getColor(context.resources, R.color.petroleum, context.theme)
    set.setCircleColor(ResourcesCompat.getColor(context.resources, R.color.petroleum, context.theme))
    set.lineWidth = 2f
    set.isHighlightEnabled = false
    set.valueFormatter = LargeValueFormatter()
    return set
}

fun generatePulseGraphLine(context: Context): LineDataSet {
    val set = LineDataSet(mutableListOf(), context.resources.getString(R.string.pulse))
    set.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
    set.color = ResourcesCompat.getColor(context.resources, R.color.severity_green, context.theme)
    set.setCircleColor(ResourcesCompat.getColor(context.resources, R.color.severity_green, context.theme))
    set.lineWidth = 2f
    set.isHighlightEnabled = false
    set.valueFormatter = LargeValueFormatter()
    return set
}