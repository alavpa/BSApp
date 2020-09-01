package com.alavpa.bsproducts.main

import android.widget.ImageView
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.view.get
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.alavpa.bsproducts.R
import com.alavpa.bsproducts.custom.RecyclerViewCountAssertion
import com.alavpa.bsproducts.custom.SwipeToRefreshMatchers
import com.alavpa.bsproducts.details.DetailsActivity
import com.alavpa.bsproducts.presentation.main.MainPresenter
import com.alavpa.bsproducts.presentation.model.ProductItem
import com.alavpa.bsproducts.utils.loader.ImageLoader
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4::class)
class DetailsActivityTest {

    private val imageLoader: ImageLoader = mock()
    private val presenter: DetailsPresenter = mock()
    private val liveData = MutableLiveData<DetailsPresenter.ViewModel>()

    @Rule
    @JvmField
    var rule = ActivityTestRule(DetailsActivity::class.java, true, false)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        loadKoinModules(
            module(override = true) {
                viewModel { presenter }
                single { imageLoader }
            }
        )

        given(presenter.renderLiveData).willReturn(liveData)
    }
}
