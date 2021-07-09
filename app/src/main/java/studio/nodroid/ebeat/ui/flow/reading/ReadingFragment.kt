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
import studio.nodroid.ebeat.analytics.Analytics
import studio.nodroid.ebeat.analytics.AnalyticsScreen
import studio.nodroid.ebeat.model.PressureSeverity
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
    private val analytics by inject<Analytics>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (savedInstanceState == null) {
            analytics.logScreenEvent(AnalyticsScreen.ADD_READING)
        }
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        return inflater.inflate(R.layout.fragment_reading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        motionLayout.doOnApplyWindowInsets { target, insets, initialState ->
            target.updatePadding(
                bottom = initialState.paddings.bottom + insets.systemWindowInsetBottom,
                top = initialState.paddings.top + insets.systemWindowInsetTop
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
        viewModel.readingSeverity.observe(viewLifecycleOwner, Observer { showPressureSeverity(it) })

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
            motionLayout.transitionToState(R.id.enterDescription)
            showKeyboard(descriptionValue)
        }

        actionDiscardConfirm.setOnClickListener { viewModel.confirmedDiscard() }
        actionDiscardAbort.setOnClickListener { viewModel.abortedDiscard() }

        actionSave.setOnClickListener { viewModel.saveReading() }
        actionDiscard.setOnClickListener { viewModel.discardReading() }
        actionDone.setOnClickListener {
            viewModel.savedNotificationDismissed()
            navigateBack()
        }

        viewModel.events.observe(viewLifecycleOwner, Observer { event ->
            when (event) {
                is ReadingViewModel.State.TimeNeeded -> timePicker.show(childFragmentManager, "time")
                is ReadingViewModel.State.DateNeeded -> datePicker.show(childFragmentManager, "date")
                is ReadingViewModel.State.Saved -> showSavedReading()
                is ReadingViewModel.State.Done -> navigateBack()
                is ReadingViewModel.State.AskDiscard -> showDiscard(event.isDescriptionSet)
                is ReadingViewModel.State.FinishedDiscard -> showActions(event.isDescriptionSet)

                null -> {/*noop*/
                }
            }
        })
    }

    private fun showPressureSeverity(bpSeverity: PressureSeverity?) {
        severity.text = when (bpSeverity) {
            PressureSeverity.ERROR -> getString(R.string.severity_error)
            PressureSeverity.NORMAL -> getString(R.string.severity_normal)
            PressureSeverity.ELEVATED -> getString(R.string.severity_elevated)
            PressureSeverity.HYPERTENSION_1 -> getString(R.string.severity_hypertension1)
            PressureSeverity.HYPERTENSION_2 -> getString(R.string.severity_hypertension2)
            PressureSeverity.HYPERTENSION_CRISIS -> getString(R.string.severity_crisis)
            null -> getString(R.string.severity_error)
        }
    }

    private fun showActions(isDescriptionSet: Boolean) {
        if (isDescriptionSet) {
            motionLayout.transitionToState(R.id.chooseActionDesc)
        } else {
            motionLayout.transitionToState(R.id.chooseActionNoDesc)
        }
    }

    private fun showDiscard(isDescriptionSet: Boolean) {
        if (isDescriptionSet) {
            motionLayout.transitionToState(R.id.discardDesc)
        } else {
            motionLayout.transitionToState(R.id.discardNoDesc)
        }
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
        user.text = boldFormat(getString(R.string.reading_user), selectedUser.name)
        motionLayout.transitionToState(R.id.selectDate)
    }

    private fun showUserPicker(list: List<User>) {
        motionLayout.transitionToState(R.id.selectUser)
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
        dateTime.text = boldFormat(getString(R.string.reading_date), time.toDate(), time.toTime())
        motionLayout.transitionToState(R.id.enterSystolic)
        systolicValue.requestFocus()
        showKeyboard(systolicValue)
    }


    /**
     *
     * Systolic handling
     *
     */

    private fun showSystolicSelected(sys: Int) {
        systolic.text = boldFormat(getString(R.string.reading_systolic), sys.toString())
        motionLayout.transitionToState(R.id.enterDiastolic)
        diastolicValue.requestFocus()
        showKeyboard(diastolicValue)
    }


    /**
     *
     * Diastolic handling
     *
     */

    private fun showDiastolicSelected(dia: Int) {
        diastolic.text = boldFormat(getString(R.string.reading_diastolic), dia.toString())
        motionLayout.transitionToState(R.id.enterPulse)
        pulseValue.requestFocus()
        showKeyboard(pulseValue)
    }


    /**
     *
     * Pulse handling
     *
     */

    private fun showPulseSelected(pul: Int) {
        pulse.text = boldFormat(getString(R.string.reading_pulse), pul.toString())
        motionLayout.transitionToState(R.id.chooseActionNoDesc)
    }


    /**
     *
     * Description handling
     *
     */

    private fun showDescriptionSelected(desc: String) {
        if (desc.isEmpty()) {
            motionLayout.transitionToState(R.id.chooseActionNoDesc)
        } else {
            description.text = boldFormat("{first}", desc)
            actionDescription.visibility = View.GONE
            motionLayout.transitionToState(R.id.chooseActionDesc)
        }
    }
}
