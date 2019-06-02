package studio.nodroid.ebeat.ui.flow.reading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_reading.*
import studio.nodroid.ebeat.R
import studio.nodroid.ebeat.utils.colorFormat

class ReadingFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reading, container, false)
    }

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
            motionLayout.transitionToState(R.id.userSet)
        }
    }

}
