package studio.nodroid.ebeat.ui.flow.reading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.transition.TransitionInflater
import com.google.android.material.chip.Chip
import dev.chrisbanes.insetter.doOnApplyWindowInsets
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

    private val viewModel by inject<ReadingViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        return inflater.inflate(R.layout.fragment_reading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        motionLayout.doOnApplyWindowInsets { target, insets, initialState ->
            target.updatePadding(
                bottom = initialState.paddings.bottom + insets.systemWindowInsetBottom
            )
        }

        motionLayout.transitionToState(R.id.initial)

        icon.startDotAnimation()

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

        systolicValueConfirm.setOnClickListener {
            viewModel.systolicPressureEntered(systolicValue.text.toString())
        }
        systolicValue.setOnEditorActionListener { _, _, _ ->
            viewModel.systolicPressureEntered(systolicValue.text.toString())
            true
        }

        diastolicValueConfirm.setOnClickListener {
            viewModel.diastolicPressureEntered(diastolicValue.text.toString())
        }
        diastolicValue.setOnEditorActionListener { _, _, _ ->
            viewModel.diastolicPressureEntered(diastolicValue.text.toString())
            true
        }

        pulseValueConfirm.setOnClickListener {
            viewModel.pulseEntered(pulseValue.text.toString())
            hideKeyboard(it)
        }
        pulseValue.setOnEditorActionListener { v, _, _ ->
            viewModel.pulseEntered(pulseValue.text.toString())
            hideKeyboard(v)
            true
        }

        descriptionValueConfirm.setOnClickListener {
            viewModel.descriptionEntered(descriptionValue.text.toString())
            hideKeyboard(it)
        }
        descriptionValue.setOnEditorActionListener { v, _, _ ->
            viewModel.descriptionEntered(descriptionValue.text.toString())
            hideKeyboard(v)
            true
        }

        actionDescription.setOnClickListener {
            motionLayout.transitionToState(R.id.descriptionRequired)
            showKeyboard(descriptionValue)
        }

        actionSave.setOnClickListener { viewModel.saveReading() }
        actionDiscard.setOnClickListener { viewModel.discardReading() }
        actionDone.setOnClickListener { viewModel.savedNotificationDismissed() }

        viewModel.events.observe(viewLifecycleOwner, Observer { event ->
            when (event) {
                ReadingViewModel.State.TIME_NEEDED -> timePicker.show(childFragmentManager, "time")
                ReadingViewModel.State.DATE_NEEDED -> datePicker.show(childFragmentManager, "date")
                ReadingViewModel.State.SAVED -> showSavedReading()
                ReadingViewModel.State.DONE -> navigateBack()

                null -> {/*noop*/
                }
            }
        })
    }

    private fun showSavedReading() {
        motionLayout.transitionToState(R.id.readingSaved)
    }

    private fun navigateBack() {
        user.findNavController().popBackStack()
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
        list.forEach { user ->
            val chip = layoutInflater.inflate(R.layout.item_user, null) as Chip
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.marginStart = requireContext().dpPx(8f)
            chip.layoutParams = layoutParams
            chip.text = user.name
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
        showKeyboard(diastolicValue)
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
        showKeyboard(pulseValue)
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
