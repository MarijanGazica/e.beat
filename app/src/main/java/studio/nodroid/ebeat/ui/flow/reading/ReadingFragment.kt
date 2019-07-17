package studio.nodroid.ebeat.ui.flow.reading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.fragment_reading.*
import org.koin.android.ext.android.inject
import studio.nodroid.ebeat.R
import studio.nodroid.ebeat.model.User
import studio.nodroid.ebeat.ui.dateTime.DatePickDialog
import studio.nodroid.ebeat.ui.dateTime.TimePickDialog
import studio.nodroid.ebeat.utils.colorFormat
import studio.nodroid.ebeat.utils.resolveColor
import studio.nodroid.ebeat.utils.toDate
import studio.nodroid.ebeat.utils.toTime

class ReadingFragment : Fragment() {

    var step = 0


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

        user.text = colorFormat(getString(R.string.reading_user), "Marijan")
        dateTime.text = colorFormat(getString(R.string.reading_date), "31.05.2019.", "13:01")
        systolic.text = colorFormat(getString(R.string.reading_systolic), "121")
        diastolic.text = colorFormat(getString(R.string.reading_diastolic), "59")
        pulse.text = colorFormat(getString(R.string.reading_pulse), "55")
        description.text = "Some description that's also added just so we can see how it looks"

        viewModel.userList.observe(viewLifecycleOwner, Observer { list ->
            when (list.size) {
                1 -> setUser(list[0])
                else -> showUserPicker(list)
            }
        })

        viewModel.selectedUser.observe(viewLifecycleOwner, Observer { it?.run { showUserSelected(this) } })

        icon.setOnClickListener {
            Toast.makeText(requireContext(), "$step", Toast.LENGTH_SHORT).show()
            when (step) {
                0 -> {
                    motionLayout.transitionToState(R.id.userSet)
                    step++
                }
                1 -> {
                    motionLayout.transitionToState(R.id.dateSet)
                    step++
                }
                3 -> {
                    motionLayout.transitionToState(R.id.systolicSet)
                    step++
                }
                4 -> {
                    motionLayout.transitionToState(R.id.diastolicSet)
                    step++
                }
                5 -> {
                    motionLayout.transitionToState(R.id.pulseSet)
                    step++
                }
                6 -> {
                    motionLayout.transitionToState(R.id.descriptionSet)
                    step++
                }
                else -> {
                    motionLayout.transitionToState(R.id.start)
                    step = 0
                }
            }
        }

        timeNow.setOnClickListener { viewModel.readingTakenNow() }
        timeOther.setOnClickListener { viewModel.timeNotNowSelected() }

        viewModel.selectedTime.observe(viewLifecycleOwner, Observer { it?.run { showTimeSelected(this) } })
        viewModel.events.observe(viewLifecycleOwner, Observer { event ->
            when (event) {
                ReadingDetailsViewModel.Action.TIME_NEEDED -> timePicker.show(childFragmentManager, "time")
                ReadingDetailsViewModel.Action.DATE_NEEDED -> datePicker.show(childFragmentManager, "date")
                null -> {/*noop*/
                }
            }
        })
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
    }

}
