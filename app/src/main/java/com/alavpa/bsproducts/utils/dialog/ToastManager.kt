package com.alavpa.bsproducts.utils.dialog

import android.widget.Toast
import androidx.fragment.app.FragmentActivity

class ToastManager {
    fun show(activity: FragmentActivity, resId: Int) {
        Toast.makeText(activity, resId, Toast.LENGTH_LONG).show()
    }
}
