package com.alavpa.bsproducts.custom

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import org.hamcrest.Matchers.`is`

class RecyclerViewCountAssertion(private val expectedCount: Int) : ViewAssertion {
    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        if (noViewFoundException != null) {
            throw noViewFoundException
        }

        val rv = view as RecyclerView
        val adapter = rv.adapter as RecyclerView.Adapter
        assertThat(expectedCount, `is`(adapter.itemCount))
    }
}
