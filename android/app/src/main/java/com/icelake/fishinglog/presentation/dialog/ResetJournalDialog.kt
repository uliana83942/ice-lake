package com.icelake.fishinglog.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.icelake.fishinglog.R
import com.icelake.fishinglog.core.di.ServiceLocator
import com.icelake.fishinglog.databinding.DialogResetJournalBinding

class ResetJournalDialog : DialogFragment() {

    private var binding: DialogResetJournalBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.Theme_IceLake_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflated = DialogResetJournalBinding.inflate(inflater, container, false)
        binding = inflated
        return inflated.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val current = binding ?: return
        current.btnResetCancel.setOnClickListener { dismissAllowingStateLoss() }
        current.btnResetConfirm.setOnClickListener { performReset() }
    }

    private fun performReset() {
        ServiceLocator.tripRepository().clearJournal()
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
        binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "dialog_reset_journal"
        const val RESULT_KEY = "result_reset_journal"
        private const val DIALOG_MARGIN_DP = 20f
    }
}
