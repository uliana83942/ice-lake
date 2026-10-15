package com.icelake.fishinglog.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.icelake.fishinglog.R
import com.icelake.fishinglog.core.di.ServiceLocator
import com.icelake.fishinglog.core.util.MeasureFormatter
import com.icelake.fishinglog.databinding.DialogSpotLibraryBinding
import com.icelake.fishinglog.domain.model.Spot

class SpotLibraryDialog : DialogFragment() {

    private var binding: DialogSpotLibraryBinding? = null
    private var adapter: SpotAdapter? = null
    private var pending: Spot? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.Theme_IceLake_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflated = DialogSpotLibraryBinding.inflate(inflater, container, false)
        binding = inflated
        return inflated.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val current = binding ?: return
        val settings = ServiceLocator.settingsRepository().settings()
        val formatter = MeasureFormatter(current.root.context)
        val spotRepository = ServiceLocator.spotRepository()
        val selected = spotRepository.selectedSpot()
        pending = selected
        val created = SpotAdapter(formatter, settings.metricUnits) { spot -> pending = spot }
        adapter = created
        current.listSpots.layoutManager = LinearLayoutManager(requireContext())
        current.listSpots.adapter = created
        created.submit(ServiceLocator.spotsUseCase()(), selected.id)
        current.btnSpotDialogClose.setOnClickListener { dismissAllowingStateLoss() }
        current.btnSpotUse.setOnClickListener { applySelection() }
    }

    private fun applySelection() {
        val chosen = pending
        if (chosen != null) {
            ServiceLocator.spotRepository().selectSpot(chosen.id)
        }
        parentFragmentManager.setFragmentResult(RESULT_KEY, Bundle())
        dismissAllowingStateLoss()
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window ?: return
        val metrics = resources.displayMetrics
        val inset = (DIALOG_MARGIN_DP * metrics.density * 2).toInt()
        window.setLayout(
            metrics.widthPixels - inset,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onDestroyView() {
        val current = binding
        if (current != null) {
            current.listSpots.adapter = null
        }
        adapter = null
        binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "dialog_spot_library"
        const val RESULT_KEY = "result_spot_library"
        private const val DIALOG_MARGIN_DP = 20f
    }
}
