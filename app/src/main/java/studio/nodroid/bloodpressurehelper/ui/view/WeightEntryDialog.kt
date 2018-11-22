package studio.nodroid.bloodpressurehelper.ui.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import kotlinx.android.synthetic.main.dialog_weight_entry.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import studio.nodroid.bloodpressurehelper.R
import studio.nodroid.bloodpressurehelper.room.UserRepository
import studio.nodroid.bloodpressurehelper.sharedPrefs.SharedPrefs
import studio.nodroid.bloodpressurehelper.utils.roundTo
import java.text.DecimalFormat

class WeightEntryDialog : DialogFragment() {

    private var format = DecimalFormat("0.#")

    private val viewModel: WeightEntryViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_weight_entry, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        viewModel.activeUser.observe(
            this,
            Observer {
                if (it.weight > 0) {
                    weightInput.setText(format.format(it.weight).toString(), TextView.BufferType.EDITABLE)
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weightInput.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveWeight()
            }
            false
        }

        ok.setOnClickListener {
            saveWeight()
        }
    }

    private fun saveWeight() {
        val weight = if (weightInput.text.toString().isNotEmpty()) weightInput.text.toString().toDouble() else 0.0
        viewModel.setNewWeight(weight)
        dismiss()
    }
}

class WeightEntryViewModel(
    sharedPrefs: SharedPrefs,
    private val userRepository: UserRepository
) : ViewModel() {
    val activeUser = userRepository.getUserById(sharedPrefs.getLastUserId())

    fun setNewWeight(weight: Double) {
        userRepository.updateUser(activeUser.value?.copy(weight = weight.roundTo(2)))
    }
}