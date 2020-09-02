package com.alavpa.bsproducts.main

import android.widget.ImageView
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.view.get
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alavpa.bsproducts.R
import com.alavpa.bsproducts.custom.RecyclerViewCountAssertion
import com.alavpa.bsproducts.custom.SwipeToRefreshMatchers
import com.alavpa.bsproducts.presentation.main.MainPresenter
import com.alavpa.bsproducts.presentation.model.ProductItem
import com.alavpa.bsproducts.utils.loader.ImageLoader
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private val imageLoader: ImageLoader = mock()
    private val presenter: MainPresenter = mock()
    private val liveData = MutableLiveData<MainPresenter.ViewModel>()

    private lateinit var scenario: ActivityScenario<MainActivity>

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
        scenario = ActivityScenario.launch(MainActivity::class.java)

        scenario.onActivity {
            verify(presenter).load()
        }
    }

    @Test
    fun when_is_loading_show_loader() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        liveData.value = MainPresenter.ViewModel(
            isLoading = true
        )

        onView(withId(R.id.pull_to_refresh)).check(matches(SwipeToRefreshMatchers.isRefreshing()))
    }

    @Test
    fun load_items_on_recycler_view() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        liveData.value = MainPresenter.ViewModel(
            items = listOf(
                provideProductItem(1),
                provideProductItem(2),
                provideProductItem(3)
            )
        )
        onView(withId(R.id.rv_products)).check(RecyclerViewCountAssertion(3))
    }

    @Test
    fun load_image_url() {
        scenario = ActivityScenario.launch(MainActivity::class.java)

        liveData.value = MainPresenter.ViewModel(
            items = listOf(
                provideProductItem(1),
                provideProductItem(2),
                provideProductItem(3)
            )
        )

        scenario.onActivity {
            val image1 = it.findViewById<RecyclerView>(R.id.rv_products)[0]
                .findViewById<ImageView>(R.id.image)
            val image2 = it.findViewById<RecyclerView>(R.id.rv_products)[1]
                .findViewById<ImageView>(R.id.image)
            val image3 = it.findViewById<RecyclerView>(R.id.rv_products)[2]
                .findViewById<ImageView>(R.id.image)

            verify(imageLoader).load(it, "image1", image1)
            verify(imageLoader).load(it, "image2", image2)
            verify(imageLoader).load(it, "image3", image3)
        }
    }

    @Test
    fun check_title() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText("BSProducts"))))
    }

    @Test
    fun on_click_item() {
        scenario = ActivityScenario.launch(MainActivity::class.java)

        liveData.value = MainPresenter.ViewModel(
            items = listOf(
                provideProductItem(1),
                provideProductItem(2),
                provideProductItem(3)
            )
        )

        onView(withId(R.id.rv_products)).perform(
            RecyclerViewActions.actionOnItemAtPosition<MainAdapter.Item>(0, click())
        )

        verify(presenter).clickOn(
            provideProductItem(1)
        )
    }

    @Test
    fun show_server_exception_dialog() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        liveData.value = MainPresenter.ViewModel(
            showServerException = Pair(true, "user")
        )

        onView(withText("user")).check(matches(isDisplayed()))
    }

    @Test
    fun show_unknown_error_dialog() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        liveData.value = MainPresenter.ViewModel(
            showUnknownError = true
        )

        onView(withText(R.string.unknown_error)).check(matches(isDisplayed()))
    }

    @Test
    fun show_liked_item() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        liveData.value = MainPresenter.ViewModel(
            items = listOf(provideProductItem(1, true))
        )

        scenario.onActivity {
            val image1 = it.findViewById<RecyclerView>(R.id.rv_products)[0]
                .findViewById<ImageView>(R.id.like)

            verify(imageLoader).load(R.drawable.ic_baseline_favorite_24, image1)
        }
    }

    private fun provideProductItem(id: Long, liked: Boolean = false): ProductItem {
        return ProductItem(
            id,
            "name$id",
            "brand$id",
            40,
            "€",
            "image$id",
            liked
        )
    }
}
