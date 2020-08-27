package com.alavpa.bsproducts.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.alavpa.bsproducts.R
import com.alavpa.bsproducts.custom.RecyclerViewCountAssertion
import com.alavpa.bsproducts.presentation.main.MainPresenter
import com.alavpa.bsproducts.presentation.model.ProductItem
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

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private val presenter: MainPresenter = mock()
    private val liveData = MutableLiveData<MainPresenter.ViewModel>()

    @Rule
    @JvmField
    var rule = ActivityTestRule(MainActivity::class.java, true, false)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        loadKoinModules(
            module(override = true) {
                viewModel { presenter }
            }
        )

        given(presenter.renderLiveData).willReturn(liveData)
    }

    @Test
    fun on_start_activity_always_load_first_page() {
        rule.launchActivity(null)
        verify(presenter).load()
    }

    @Test
    fun when_is_loading_show_loader() {
        rule.launchActivity(null)
        rule.runOnUiThread {
            liveData.value = MainPresenter.ViewModel(
                isLoading = true
            )
        }

        onView(withId(R.id.loader)).check(matches(isDisplayed()))
    }

    @Test
    fun when_is_not_loading_show_loader() {
        rule.launchActivity(null)
        rule.runOnUiThread {
            liveData.value = MainPresenter.ViewModel(
                isLoading = false
            )
        }

        onView(withId(R.id.loader)).check(matches(not(isDisplayed())))
    }

    @Test
    fun load_items_on_recycler_view() {
        rule.launchActivity(null)
        rule.runOnUiThread {
            liveData.value = MainPresenter.ViewModel(
                items = listOf(
                    ProductItem(1, "name", "brand", 40, "€", "image"),
                    ProductItem(2, "name", "brand", 40, "€", "image"),
                    ProductItem(3, "name", "brand", 40, "€", "image")
                )
            )
        }

        onView(withId(R.id.loader)).check(RecyclerViewCountAssertion(3))
    }
}
