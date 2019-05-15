package studio.nodroid.ebeat.ui.view

import android.content.Context
import android.util.AttributeSet
import android.widget.HorizontalScrollView
import kotlinx.android.synthetic.main.view_filter.view.*
import studio.nodroid.ebeat.R
import studio.nodroid.ebeat.utils.setBackgroundColorCompat

class FilterView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : HorizontalScrollView(context, attrs, defStyleAttr) {

    private var filterSelected: (Selection) -> Unit = {}

    init {
        inflate(getContext(), R.layout.view_filter, this)

        setBackgroundColorCompat(R.color.colorPrimary)

        week.setOnClickListener {
            filterSelected(Selection.WEEK)
        }
        month.setOnClickListener {
            filterSelected(Selection.MONTH)
        }
        allTime.setOnClickListener {
            filterSelected(Selection.ALL_TIME)
        }
        range.setOnClickListener {
            range.isChecked = false
            filterSelected(Selection.RANGE)
        }
    }

    fun onFilterSelected(function: (Selection) -> Unit) {
        this.filterSelected = function
    }

//    fun setDateText(txt: String?) {
//        range.text = txt ?: context.resources.getString(R.string.range)
//    }

    fun markSelection(position: Int) {
        week.isChecked = position == 0
        month.isChecked = position == 1
        allTime.isChecked = position == 2
        range.isChecked = position == 3
    }


    enum class Selection {
        WEEK, MONTH, ALL_TIME, RANGE
    }
}