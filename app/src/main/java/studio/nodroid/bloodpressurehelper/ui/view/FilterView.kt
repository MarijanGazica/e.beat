package studio.nodroid.bloodpressurehelper.ui.view

import android.content.Context
import android.util.AttributeSet
import android.widget.HorizontalScrollView
import kotlinx.android.synthetic.main.view_filter.view.*
import studio.nodroid.bloodpressurehelper.R
import studio.nodroid.bloodpressurehelper.utils.setBackgroundColorCompat

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
        date.setOnClickListener {
            date.isChecked = false
            filterSelected(Selection.DATE)
        }
    }

    fun onFilterSelected(function: (Selection) -> Unit) {
        this.filterSelected = function
    }

    fun setDateText(txt: String?) {
        date.text = txt ?: context.resources.getString(R.string.date)
    }

    fun markSelection(position: Int) {
        week.isChecked = position == 0
        month.isChecked = position == 1
        allTime.isChecked = position == 2
        date.isChecked = position == 3
    }


    enum class Selection {
        WEEK, MONTH, ALL_TIME, DATE
    }
}