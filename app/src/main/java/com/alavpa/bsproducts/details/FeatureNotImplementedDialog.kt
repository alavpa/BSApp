package com.alavpa.bsproducts.details

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.alavpa.bsproducts.R

class FeatureNotImplementedDialog : DialogFragment() {

    companion object {
        fun newInstance(listener: FeatureNotImplementedDialogListener): FeatureNotImplementedDialog {
            return FeatureNotImplementedDialog().apply {
                this.listener = listener
            }
        }
    }

    private var listener: FeatureNotImplementedDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.run {
            AlertDialog.Builder(this)
                .setNeutralButton(android.R.string.ok) { dialog, _ ->
                    listener?.onOk()
                    dialog.dismiss()
                }
                .setMessage(R.string.feature_not_implemented)
                .setCancelable(false)
                .create()
                .apply { requestWindowFeature(Window.FEATURE_NO_TITLE) }
        } ?: super.onCreateDialog(savedInstanceState)
    }

    interface FeatureNotImplementedDialogListener {
        fun onOk()
    }
}
