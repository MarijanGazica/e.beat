package studio.nodroid.ebeat.ui.flow.reading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_reading.*
import studio.nodroid.ebeat.R
import studio.nodroid.ebeat.utils.colorFormat

class ReadingFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reading, container, false)
    }

    var step = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user.text = requireContext().colorFormat(getString(R.string.reading_user), "Marijan")
        date.text = requireContext().colorFormat(getString(R.string.reading_date), "31.05.2019.")
        time.text = requireContext().colorFormat(getString(R.string.reading_time), "17:01")
        systolic.text = requireContext().colorFormat(getString(R.string.reading_systolic), "121")
        diastolic.text = requireContext().colorFormat(getString(R.string.reading_diastolic), "59")
        pulse.text = requireContext().colorFormat(getString(R.string.reading_pulse), "55")
        description.text = "Some description that's also added just so we can see how it looks"


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
                2 -> {
                    motionLayout.transitionToState(R.id.timeSet)
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

    }
}
