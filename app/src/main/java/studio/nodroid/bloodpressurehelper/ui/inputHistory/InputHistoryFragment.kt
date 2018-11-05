package studio.nodroid.bloodpressurehelper.ui.inputHistory

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_input_history.*
import org.koin.android.ext.android.inject
import studio.nodroid.bloodpressurehelper.R
import studio.nodroid.bloodpressurehelper.room.PressureDataRepository

class InputHistoryFragment : Fragment() {

    val pressureDataRepo: PressureDataRepository by inject()
    val readingHistoryAdapter by lazy { ReadingHistoryAdapter() }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        pressureDataRepo.getAllReadings().observe(this, Observer { readingHistoryAdapter.setData(it) })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_input_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        readingList.layoutManager = LinearLayoutManager(requireContext())
        readingList.adapter = readingHistoryAdapter
    }
}