package com.icelake.fishinglog.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.materialswitch.MaterialSwitch
import com.icelake.fishinglog.R
import com.icelake.fishinglog.core.di.ServiceLocator
import com.icelake.fishinglog.databinding.DialogSettingsBinding
import com.icelake.fishinglog.domain.model.AppSettings

class SettingsDialog : DialogFragment() {

    private var binding: DialogSettingsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.Theme_IceLake_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflated = DialogSettingsBinding.inflate(inflater, container, false)
        binding = inflated
        return inflated.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val current = binding ?: return
        val settings = ServiceLocator.settingsRepository().settings()
        current.switchSound.isChecked = settings.soundEnabled
        current.switchHaptic.isChecked = settings.hapticEnabled
        current.switchUnits.isChecked = settings.metricUnits
        current.switchTemperature.isChecked = settings.showTemperature
        describe(current.switchSound)
        describe(current.switchHaptic)
        describe(current.switchUnits)
        describe(current.switchTemperature)
        renderUnitsLabel(settings.metricUnits)

        current.switchSound.setOnCheckedChangeListener { button, _ ->
            describe(button as MaterialSwitch)
            persist()
        }
        current.switchHaptic.setOnCheckedChangeListener { button, _ ->
            describe(button as MaterialSwitch)
            persist()
        }
        current.switchUnits.setOnCheckedChangeListener { button, checked ->
            describe(button as MaterialSwitch)
            renderUnitsLabel(checked)
            persist()
        }
        current.switchTemperature.setOnCheckedChangeListener { button, _ ->
            describe(button as MaterialSwitch)
            persist()
        }
        current.btnSettingsClose.setOnClickListener { dismissAllowingStateLoss() }
        current.btnSettingsReset.setOnClickListener { confirmReset() }
    }

    private fun renderUnitsLabel(metric: Boolean) {
        val current = binding ?: return
        val label = if (metric) {
            R.string.settings_units_metric
        } else {
            R.string.settings_units_imperial
        }
        current.txtSettingsUnits.setText(label)
        current.switchUnits.contentDescription = getString(label)
    }

    private fun describe(target: MaterialSwitch) {
        val state = if (target.isChecked) {
            R.string.settings_state_on
        } else {
            R.string.settings_state_off
        }
        ViewCompat.setStateDescription(target, getString(state))
    }

    private fun persist() {
        val current = binding ?: return
        ServiceLocator.settingsRepository().update(
            AppSettings(
                soundEnabled = current.switchSound.isChecked,
                hapticEnabled = current.switchHaptic.isChecked,
                metricUnits = current.switchUnits.isChecked,
                showTemperature = current.switchTemperature.isChecked
            )
        )
        parentFragmentManager.setFragmentResult(RESULT_KEY, Bundle())
    }

    private fun confirmReset() {
        if (!isAdded || parentFragmentManager.isStateSaved) {
            return
        }
        ResetJournalDialog().show(parentFragmentManager, ResetJournalDialog.TAG)
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
        binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "dialog_settings"
        const val RESULT_KEY = "result_settings"
        private const val DIALOG_MARGIN_DP = 20f
    }
}
