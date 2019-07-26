package studio.nodroid.ebeat.ui.flow.reading

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.fragment_reading.*
import org.koin.android.ext.android.inject
import studio.nodroid.ebeat.R
import studio.nodroid.ebeat.model.User
import studio.nodroid.ebeat.ui.dateTime.DatePickDialog
import studio.nodroid.ebeat.ui.dateTime.TimePickDialog
import studio.nodroid.ebeat.utils.*

class ReadingFragment : Fragment() {

    private val timePicker by lazy {
        TimePickDialog().apply {
            this@apply.onTimeChosen = {
                viewModel.timeSelected(it)
            }
        }
    }

    private val datePicker by lazy {
        DatePickDialog().apply {
            this@apply.onDateChosen = {
                viewModel.dateSelected(it)
            }
        }
    }

    val viewModel by inject<ReadingDetailsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        motionLayout.transitionToState(R.id.initial)

        viewModel.userList.observe(viewLifecycleOwner, Observer { list ->
            when (list.size) {
                1 -> setUser(list[0])
                else -> showUserPicker(list)
            }
        })

        viewModel.selectedUser.observe(viewLifecycleOwner, Observer { it?.run { showUserSelected(this) } })
        viewModel.selectedTime.observe(viewLifecycleOwner, Observer { it?.run { showTimeSelected(this) } })
        viewModel.selectedSystolic.observe(viewLifecycleOwner, Observer { it?.run { showSystolicSelected(this) } })
        viewModel.selectedDiastolic.observe(viewLifecycleOwner, Observer { it?.run { showDiastolicSelected(this) } })
        viewModel.selectedPulse.observe(viewLifecycleOwner, Observer { it?.run { showPulseSelected(this) } })
        viewModel.selectedDescription.observe(viewLifecycleOwner, Observer { it?.run { showDescriptionSelected(this) } })

        timeNow.setOnClickListener { viewModel.readingTakenNow() }
        timeOther.setOnClickListener { viewModel.timeNotNowSelected() }
        systolicValue.setOnEditorActionListener { _, _, _ ->
            viewModel.systolicPressureEntered(systolicValue.text.toString())
            true
        }
        diastolicValue.setOnEditorActionListener { _, _, _ ->
            viewModel.diastolicPressureEntered(diastolicValue.text.toString())
            true
        }
        pulseValue.setOnEditorActionListener { v, _, _ ->
            viewModel.pulseEntered(pulseValue.text.toString())
            hideKeyboard(v)
            true
        }
        descriptionValue.setOnEditorActionListener { v, _, _ ->
            viewModel.descriptionEntered(descriptionValue.text.toString())
            hideKeyboard(v)
            true
        }

        actionDescription.setOnClickListener { motionLayout.transitionToState(R.id.descriptionRequired) }
        actionSave.setOnClickListener { viewModel.saveReading() }
        actionDiscard.setOnClickListener { viewModel.discardReading() }

        viewModel.events.observe(viewLifecycleOwner, Observer { event ->
            when (event) {
                ReadingDetailsViewModel.Action.TIME_NEEDED -> timePicker.show(childFragmentManager, "time")
                ReadingDetailsViewModel.Action.DATE_NEEDED -> datePicker.show(childFragmentManager, "date")
                ReadingDetailsViewModel.Action.SAVED -> handleSuccess()
                ReadingDetailsViewModel.Action.CANCELED -> {
                    handleSuccess()
                }
                null -> {/*noop*/
                }
            }
        })
    }

    private fun handleSuccess() {
        icon.findNavController().popBackStack()
    }

    /**
     *
     * User picking
     *
     */

    private fun showUserSelected(selectedUser: User) {
        user.text = colorFormat(getString(R.string.reading_user), selectedUser.name)
        motionLayout.transitionToState(R.id.userSet)
    }

    private fun showUserPicker(list: List<User>) {
        motionLayout.transitionToState(R.id.start)
        Log.d("findme", "start")
        list.forEach { user ->
            val chip = Chip(requireContext())
            chip.text = user.name
            chip.setTextAppearanceResource(R.style.Text_Subtitle1)
            chip.setTextColor(requireContext().resolveColor(R.color.colorAccent))
            chip.setChipBackgroundColorResource(R.color.white)
            chip.setOnClickListener { viewModel.selectedUser(user) }
            chipGroup.addView(chip)
        }
    }

    private fun setUser(user: User) {
        viewModel.selectedUser(user)
    }

    /**
     *
     * Time and date picking
     *
     */

    private fun showTimeSelected(time: Long) {
        dateTime.text = colorFormat(getString(R.string.reading_date), time.toDate(), time.toTime())
        motionLayout.transitionToState(R.id.dateSet)
        systolicValue.requestFocus()
        showKeyboard(systolicValue)
    }


    /**
     *
     * Systolic handling
     *
     */

    private fun showSystolicSelected(sys: Int) {
        systolic.text = colorFormat(getString(R.string.reading_systolic), sys.toString())
        motionLayout.transitionToState(R.id.systolicSet)
        diastolicValue.requestFocus()
    }


    /**
     *
     * Diastolic handling
     *
     */

    private fun showDiastolicSelected(dia: Int) {
        diastolic.text = colorFormat(getString(R.string.reading_diastolic), dia.toString())
        motionLayout.transitionToState(R.id.diastolicSet)
        pulseValue.requestFocus()
    }


    /**
     *
     * Pulse handling
     *
     */

    private fun showPulseSelected(pul: Int) {
        pulse.text = colorFormat(getString(R.string.reading_pulse), pul.toString())
        motionLayout.transitionToState(R.id.actionsPresented)
    }


    /**
     *
     * Description handling
     *
     */

    private fun showDescriptionSelected(desc: String) {
        if (desc.isEmpty()) {
            motionLayout.transitionToState(R.id.actionsPresented)
        } else {
            description.text = colorFormat("{first}", desc)
            actionDescription.visibility = View.GONE
            motionLayout.transitionToState(R.id.descriptionSet)
        }
    }
}
