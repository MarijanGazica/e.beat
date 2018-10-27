package studio.nodroid.bloodpressurehelper.ui.pressureInput

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_pressure_input.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import studio.nodroid.bloodpressurehelper.R
import studio.nodroid.bloodpressurehelper.vm.PressureInputViewModel

class PressureInputFragment : Fragment() {

    private val viewModel: PressureInputViewModel by viewModel()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pressure_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pressureInput.fragmentManager = childFragmentManager
        pressureInput.pressureData.observe(this, Observer { viewModel.pressureDataChanged(it) })

        saveReading.setOnClickListener { viewModel.saveReading() }
    }
}