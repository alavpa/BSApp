package com.alavpa.bsproducts.utils.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class ServerDialog : DialogFragment() {

    companion object {
        private const val ARGS_MESSAGE = "ARGS_MESSAGE"
        fun newInstance(message: String, listener: ServerDialogListener): ServerDialog {
            return ServerDialog().apply {
                this.listener = listener
                arguments = Bundle().apply { putString(ARGS_MESSAGE, message) }
            }
        }
    }

    private var listener: ServerDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.run {
            AlertDialog.Builder(this)
                .setNeutralButton(android.R.string.cancel) { dialog, _ ->
                    listener?.onOk()
                    dialog.dismiss()
                }
                .setMessage(arguments?.getString(ARGS_MESSAGE, "") ?: "")
                .setCancelable(false)
                .create()
                .apply { requestWindowFeature(Window.FEATURE_NO_TITLE) }
        } ?: super.onCreateDialog(savedInstanceState)
    }

    interface ServerDialogListener {
        fun onOk()
    }
}
