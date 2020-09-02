package com.alavpa.bsproducts.utils.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.alavpa.bsproducts.R

class UnknownErrorDialog : DialogFragment() {

    companion object {

        fun newInstance(listener: UnknownErrorDialogListener): UnknownErrorDialog {
            return UnknownErrorDialog().apply {
                this.listener = listener
            }
        }
    }

    private var listener: UnknownErrorDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.run {
            AlertDialog.Builder(this)
                .setNeutralButton(android.R.string.cancel) { dialog, _ ->
                    listener?.onOk()
                    dialog.dismiss()
                }
                .setMessage(R.string.unknown_error)
                .setCancelable(false)
                .create()
                .apply { requestWindowFeature(Window.FEATURE_NO_TITLE) }
        } ?: super.onCreateDialog(savedInstanceState)
    }

    interface UnknownErrorDialogListener {
        fun onOk()
    }
}
