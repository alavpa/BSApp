package com.alavpa.bsproducts.details

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.alavpa.bsproducts.R

class NoStockDialog : DialogFragment() {

    companion object {
        fun newInstance(listener: NoStockDialogListener): NoStockDialog {
            return NoStockDialog().apply {
                this.listener = listener
            }
        }
    }

    private var listener: NoStockDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.run {
            AlertDialog.Builder(this)
                .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                    listener?.onCancel()
                    dialog.dismiss()
                }
                .setPositiveButton(R.string.notify) { dialog, _ ->
                    listener?.onNotify()
                    dialog.dismiss()
                }
                .setMessage(R.string.no_stock)
                .setCancelable(false)
                .create()
                .apply { requestWindowFeature(Window.FEATURE_NO_TITLE) }
        } ?: super.onCreateDialog(savedInstanceState)
    }

    interface NoStockDialogListener {
        fun onNotify()
        fun onCancel()
    }
}
