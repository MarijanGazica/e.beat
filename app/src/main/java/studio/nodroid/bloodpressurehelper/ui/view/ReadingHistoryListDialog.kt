package studio.nodroid.bloodpressurehelper.ui.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_pressure_history_list.view.*
import kotlinx.android.synthetic.main.item_pressure_history.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import studio.nodroid.bloodpressurehelper.R
import studio.nodroid.bloodpressurehelper.model.PressureDataDB
import studio.nodroid.bloodpressurehelper.room.PressureDataRepository
import studio.nodroid.bloodpressurehelper.sharedPrefs.SharedPrefs
import java.util.*

class ReadingHistoryListDialog : DialogFragment() {

    private val readingListAdapter by lazy { ReadingListAdapter() }

    private val viewModel: ReadingHistoryListViewModel by viewModel()

    var date = Date()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        readingListAdapter.setData(emptyList())
    }

    override fun onStart() {
        super.onStart()
        viewModel.readings.observe(this, Observer { readingListAdapter.setData(it) })
        viewModel.filterByDate(date)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_pressure_history_list, container, false)
        view.pressureList.layoutManager = LinearLayoutManager(requireContext())
        view.pressureList.adapter = readingListAdapter
        return view
    }
}

class ReadingHistoryListViewModel(
    private val pressureDataRepository: PressureDataRepository,
    private val sharedPreferences: SharedPrefs
) : ViewModel() {

    fun filterByDate(date: Date?) = date?.run {
        GlobalScope.launch(Dispatchers.Main) {
            readings.value = pressureDataRepository.getReadingsForDate(this@run)
                .filter { it.userId == sharedPreferences.getLastUserId() }
        }
    }

    val readings = MutableLiveData<List<PressureDataDB>>()
}

private class ReadingListAdapter : RecyclerView.Adapter<InputViewHolder>() {

    private val data = mutableListOf<PressureDataDB>()

    fun setData(list: List<PressureDataDB>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InputViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pressure_history, parent, false)
        return InputViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: InputViewHolder, position: Int) {
        holder.bind(data[position])
    }
}

private class InputViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(pressureDataDB: PressureDataDB) {
        itemView.systolicValue.text = pressureDataDB.systolic.toString()
    }

}