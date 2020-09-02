package com.alavpa.bsproducts.details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.alavpa.bsproducts.R
import com.alavpa.bsproducts.custom.SwipeToRefreshMatchers
import com.alavpa.bsproducts.presentation.details.DetailsPresenter
import com.alavpa.bsproducts.utils.loader.ImageLoader
import com.nhaarman.mockitokotlin2.*
import kotlinx.android.synthetic.main.activity_details.*
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

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

    @Test
    fun on_pull_refresh() {
        rule.launchActivity(null)

        onView(withChild(withId(R.id.tv_brand))).perform(swipeDown())
        verify(presenter, times(2)).load(any())
    }

    @Test
    fun onAddToCart() {
        rule.launchActivity(null)

        onView(withId(R.id.btn_add)).perform(click())
        verify(presenter).onAddToCart()
    }

    @Test
    fun check_activity_title() {
        rule.launchActivity(null)

        rule.runOnUiThread {
            liveData.value = DetailsPresenter.ViewModel(
                title = "title"
            )
        }

        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText("title"))))
    }

    @Test
    fun check_image() {
        rule.launchActivity(null)

        rule.runOnUiThread {
            liveData.value = DetailsPresenter.ViewModel(
                image = "image"
            )
        }

        verify(imageLoader).load(rule.activity, "image", rule.activity.image)
    }

    @Test
    fun check_brand() {
        rule.launchActivity(null)

        rule.runOnUiThread {
            liveData.value = DetailsPresenter.ViewModel(
                brand = "brand"
            )
        }

        onView(withId(R.id.tv_brand)).check(matches(withText("brand")))
    }

    @Test
    fun check_description() {
        rule.launchActivity(null)

        rule.runOnUiThread {
            liveData.value = DetailsPresenter.ViewModel(
                description = "description"
            )
        }

        onView(withId(R.id.tv_description)).check(matches(withText("description")))
    }

    @Test
    fun check_price() {
        rule.launchActivity(null)

        rule.runOnUiThread {
            liveData.value = DetailsPresenter.ViewModel(
                price = "price"
            )
        }

        onView(withId(R.id.tv_price)).check(matches(withText("price")))
    }

    @Test
    fun check_priceWithDiscount() {
        rule.launchActivity(null)

        rule.runOnUiThread {
            liveData.value = DetailsPresenter.ViewModel(
                priceWithDiscount = "price"
            )
        }

        onView(withId(R.id.tv_discount)).check(matches(withText("price")))
    }

    @Test
    fun check_isLoading() {
        rule.launchActivity(null)
        rule.runOnUiThread {
            liveData.value = DetailsPresenter.ViewModel(
                isLoading = true
            )
        }

        onView(withId(R.id.pull_to_refresh)).check(
            matches(SwipeToRefreshMatchers.isRefreshing())
        )
    }

    @Test
    fun check_isNotLoading() {
        rule.launchActivity(null)
        rule.runOnUiThread {
            liveData.value = DetailsPresenter.ViewModel(
                isLoading = false
            )
        }

        onView(withId(R.id.pull_to_refresh)).check(
            matches(Matchers.not(SwipeToRefreshMatchers.isRefreshing()))
        )
    }

    @Test
    fun show_no_stock_error() {
        rule.launchActivity(null)
        rule.runOnUiThread {
            liveData.value = DetailsPresenter.ViewModel(
                showNoStockError = true
            )
        }

        onView(withText(R.string.no_stock)).check(matches(isDisplayed()))
    }

    @Test
    fun show_feature_not_implemented_error() {
        rule.launchActivity(null)
        rule.runOnUiThread {
            liveData.value = DetailsPresenter.ViewModel(
                showFeatureNotImplementedError = true
            )
        }

        onView(withText(R.string.feature_not_implemented)).check(matches(isDisplayed()))
    }

    @Test
    fun show_server_exception_dialog() {
        rule.launchActivity(null)
        rule.runOnUiThread {
            liveData.value = DetailsPresenter.ViewModel(
                showServerException = Pair(true, "user")
            )
        }

        onView(withText("user")).check(matches(isDisplayed()))
    }

    @Test
    fun show_unknown_error_dialog() {
        rule.launchActivity(null)
        rule.runOnUiThread {
            liveData.value = DetailsPresenter.ViewModel(
                showUnknownError = true
            )
        }

        onView(withText(R.string.unknown_error)).check(matches(isDisplayed()))
    }

    @Test
    fun on_product_added_to_cart_show_message() {
        rule.launchActivity(null)
        rule.runOnUiThread {
            liveData.value = DetailsPresenter.ViewModel(
                productAddedToCart = true
            )
        }

        onView(withText(R.string.product_added))
            .inRoot(withDecorView(not(`is`(rule.activity.window.decorView))))
            .check(matches(isDisplayed()));
    }
}
