package studio.nodroid.bloodpressurehelper.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_weight_entry.*
import studio.nodroid.bloodpressurehelper.R

class WeightEntryDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_weight_entry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weightInput.setOnEditorActionListener { v, actionId, event ->
            Toast.makeText(requireContext(), "$actionId", Toast.LENGTH_SHORT).show()
            false
        }
    }
}