package studio.nodroid.ebeat.ui.graphs

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.LargeValueFormatter
import studio.nodroid.ebeat.R

fun generateSystolicGraphLine(context: Context): LineDataSet {
    val set = LineDataSet(mutableListOf(), context.resources.getString(R.string.systolic))
    set.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
    set.color = ResourcesCompat.getColor(context.resources, R.color.color_on_background, context.theme)
    set.setCircleColor(ResourcesCompat.getColor(context.resources, R.color.color_on_background, context.theme))
    set.valueTextSize = 10f
    set.valueTextColor = ResourcesCompat.getColor(context.resources, R.color.color_on_background, context.theme)
    set.lineWidth = 2f
    set.isHighlightEnabled = false
    set.valueFormatter = LargeValueFormatter()
    return set
}

fun generateDiastolicGraphLine(context: Context): LineDataSet {
    val set = LineDataSet(mutableListOf(), context.resources.getString(R.string.diastolic))
    set.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
    set.color = ResourcesCompat.getColor(context.resources, R.color.color_on_background, context.theme)
    set.setCircleColor(ResourcesCompat.getColor(context.resources, R.color.color_on_background, context.theme))
    set.valueTextColor = ResourcesCompat.getColor(context.resources, R.color.color_on_background, context.theme)
    set.lineWidth = 2f
    set.valueTextSize = 10f
    set.isHighlightEnabled = false
    set.valueFormatter = LargeValueFormatter()
    return set
}

fun generatePulseGraphLine(context: Context): LineDataSet {
    val set = LineDataSet(mutableListOf(), context.resources.getString(R.string.pulse))
    set.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
    set.color = ResourcesCompat.getColor(context.resources, R.color.color_on_background, context.theme)
    set.setCircleColor(ResourcesCompat.getColor(context.resources, R.color.color_on_background, context.theme))
    set.valueTextColor = ResourcesCompat.getColor(context.resources, R.color.color_on_background, context.theme)
    set.valueTextSize = 10f
    set.lineWidth = 1f
    set.enableDashedLine(15f, 10f, 0f)
    set.isHighlightEnabled = false
    set.valueFormatter = LargeValueFormatter()
    return set
}