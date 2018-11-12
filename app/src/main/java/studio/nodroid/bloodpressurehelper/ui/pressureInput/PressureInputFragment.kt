package studio.nodroid.bloodpressurehelper.ui.pressureInput

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_pressure_input.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import studio.nodroid.bloodpressurehelper.R
import studio.nodroid.bloodpressurehelper.ui.view.UserPickerDialog
import studio.nodroid.bloodpressurehelper.vm.PressureInputViewModel

class PressureInputFragment : Fragment() {

    private val viewModel: PressureInputViewModel by viewModel()

    private val userPickerDialog by lazy { UserPickerDialog() }

    private val saveReadingDialog by lazy {
        AlertDialog.Builder(requireContext())
            .setPositiveButton(R.string.yes) { _, _ -> viewModel.saveReading(pressureInput.pressureData) }
            .setNegativeButton(R.string.no) { _, _ -> }
            .setTitle(R.string.are_you_sure)
            .create()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        viewModel.selectedUser.observe(this, Observer { userName.text = it.name })
        viewModel.allUsers.observe(this, Observer { viewModel.findLastUser() })
        viewModel.lastReading.observe(this, Observer { pressureInput.setData(it) })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pressure_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pressureInput.fragmentManager = childFragmentManager
        saveReading.setOnClickListener { saveReadingDialog.show() }

        userName.setOnClickListener {
            userPickerDialog.onSelect = viewModel.userSelected
            userPickerDialog.show(fragmentManager, "tag")
        }
    }

}