package com.alavpa.bsproducts.main

import android.widget.ImageView
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.view.get
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.alavpa.bsproducts.R
import com.alavpa.bsproducts.custom.RecyclerViewCountAssertion
import com.alavpa.bsproducts.custom.SwipeToRefreshMatchers
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
class MainActivityTest {

    private val imageLoader: ImageLoader = mock()
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
                single { imageLoader }
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

        onView(withId(R.id.pull_to_refresh)).check(matches(SwipeToRefreshMatchers.isRefreshing()))
    }

    @Test
    fun when_is_not_loading_show_loader() {
        rule.launchActivity(null)
        rule.runOnUiThread {
            liveData.value = MainPresenter.ViewModel(
                isLoading = false
            )
        }

        onView(withId(R.id.pull_to_refresh)).check(
            matches(not(SwipeToRefreshMatchers.isRefreshing()))
        )
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

        onView(withId(R.id.rv_products)).check(RecyclerViewCountAssertion(3))
    }

    @Test
    fun load_image_url() {
        rule.launchActivity(null)
        rule.runOnUiThread {
            liveData.value = MainPresenter.ViewModel(
                items = listOf(
                    ProductItem(1, "name", "brand", 40, "€", "image1"),
                    ProductItem(2, "name", "brand", 40, "€", "image2"),
                    ProductItem(3, "name", "brand", 40, "€", "image3")
                )
            )
        }
        sleep(100)
        val image1 = rule.activity.findViewById<RecyclerView>(R.id.rv_products)[0]
            .findViewById<ImageView>(R.id.image)
        val image2 = rule.activity.findViewById<RecyclerView>(R.id.rv_products)[1]
            .findViewById<ImageView>(R.id.image)
        val image3 = rule.activity.findViewById<RecyclerView>(R.id.rv_products)[2]
            .findViewById<ImageView>(R.id.image)

        verify(imageLoader).load(rule.activity, "image1", image1)
        verify(imageLoader).load(rule.activity, "image2", image2)
        verify(imageLoader).load(rule.activity, "image3", image3)
    }

    @Test
    fun check_title() {
        rule.launchActivity(null)
        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText("BSProducts"))))
    }
}
