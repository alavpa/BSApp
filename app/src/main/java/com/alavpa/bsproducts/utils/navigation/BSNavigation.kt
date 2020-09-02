package com.alavpa.bsproducts.utils.navigation

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.alavpa.bsproducts.details.DetailsActivity
import com.alavpa.bsproducts.presentation.utils.Navigation

class BSNavigation : Navigation {

    companion object {
        const val EXTRA_ID = "EXTRA_ID"
    }

    private var activity: FragmentActivity? = null
    fun attach(activity: FragmentActivity) {
        this.activity = activity
    }

    override fun goToProductDetails(id: Long) {
        activity?.startActivity(
            Intent(activity, DetailsActivity::class.java).apply {
                putExtra(EXTRA_ID, id)
            }
        )
    }

    override fun close() {
        activity?.finish()
    }

    fun detach() {
        activity = null
    }
}
