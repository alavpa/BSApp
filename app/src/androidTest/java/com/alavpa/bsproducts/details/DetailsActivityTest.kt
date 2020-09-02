package com.alavpa.bsproducts.details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alavpa.bsproducts.R
import com.alavpa.bsproducts.custom.DrawableMatchers
import com.alavpa.bsproducts.custom.SwipeToRefreshMatchers
import com.alavpa.bsproducts.presentation.details.DetailsPresenter
import com.alavpa.bsproducts.utils.dialog.ToastManager
import com.alavpa.bsproducts.utils.loader.ImageLoader
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import kotlinx.android.synthetic.main.activity_details.*
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
    private val toastManager: ToastManager = mock()
    private val presenter: DetailsPresenter = mock()
    private val liveData = MutableLiveData<DetailsPresenter.ViewModel>()

    private lateinit var scenario: ActivityScenario<DetailsActivity>

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        loadKoinModules(
            module(override = true) {
                viewModel { presenter }
                single { imageLoader }
                single { toastManager }
            }
        )

        given(presenter.renderLiveData).willReturn(liveData)
    }

    @Test
    fun on_pull_refresh() {
        scenario = ActivityScenario.launch(DetailsActivity::class.java)

        onView(withChild(withId(R.id.tv_brand))).perform(swipeDown())
        verify(presenter, times(2)).load(any())
    }

    @Test
    fun onAddToCart() {
        scenario = ActivityScenario.launch(DetailsActivity::class.java)

        onView(withId(R.id.btn_add)).perform(click())
        verify(presenter).onAddToCart()
    }

    @Test
    fun check_activity_title() {
        scenario = ActivityScenario.launch(DetailsActivity::class.java)


        liveData.value = DetailsPresenter.ViewModel(
            title = "title"
        )


        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText("title"))))
    }

    @Test
    fun check_image() {
        scenario = ActivityScenario.launch(DetailsActivity::class.java)


        scenario.onActivity {
            liveData.value = DetailsPresenter.ViewModel(
                image = "image"
            )

            verify(imageLoader).load(it, "image", it.image)
        }

    }

    @Test
    fun check_brand() {
        scenario = ActivityScenario.launch(DetailsActivity::class.java)

        liveData.value = DetailsPresenter.ViewModel(
            brand = "brand"
        )

        onView(withId(R.id.tv_brand)).check(matches(withText("brand")))
    }

    @Test
    fun check_description() {
        scenario = ActivityScenario.launch(DetailsActivity::class.java)

        liveData.value = DetailsPresenter.ViewModel(
            description = "description"
        )

        onView(withId(R.id.tv_description)).check(matches(withText("description")))
    }

    @Test
    fun check_price() {
        scenario = ActivityScenario.launch(DetailsActivity::class.java)

        liveData.value = DetailsPresenter.ViewModel(
            price = "price"
        )

        onView(withId(R.id.tv_price)).check(matches(withText("price")))
    }

    @Test
    fun check_priceWithDiscount() {
        scenario = ActivityScenario.launch(DetailsActivity::class.java)
        liveData.value = DetailsPresenter.ViewModel(
            priceWithDiscount = "price"
        )

        onView(withId(R.id.tv_discount)).check(matches(withText("price")))
    }

    @Test
    fun check_isLoading() {
        scenario = ActivityScenario.launch(DetailsActivity::class.java)
        liveData.value = DetailsPresenter.ViewModel(
            isLoading = true
        )

        onView(withId(R.id.pull_to_refresh)).check(
            matches(SwipeToRefreshMatchers.isRefreshing())
        )
    }

    @Test
    fun check_isNotLoading() {
        scenario = ActivityScenario.launch(DetailsActivity::class.java)
        liveData.value = DetailsPresenter.ViewModel(
            isLoading = false
        )

        onView(withId(R.id.pull_to_refresh)).check(
            matches(not(SwipeToRefreshMatchers.isRefreshing()))
        )
    }

    @Test
    fun show_no_stock_error() {
        scenario = ActivityScenario.launch(DetailsActivity::class.java)
        liveData.value = DetailsPresenter.ViewModel(
            showNoStockError = true
        )

        onView(withText(R.string.no_stock)).check(matches(isDisplayed()))
    }

    @Test
    fun show_feature_not_implemented_error() {
        scenario = ActivityScenario.launch(DetailsActivity::class.java)
        liveData.value = DetailsPresenter.ViewModel(
            showFeatureNotImplementedError = true
        )

        onView(withText(R.string.feature_not_implemented)).check(matches(isDisplayed()))
    }

    @Test
    fun show_server_exception_dialog() {
        scenario = ActivityScenario.launch(DetailsActivity::class.java)
        liveData.value = DetailsPresenter.ViewModel(
            showServerException = Pair(true, "user")
        )

        onView(withText("user")).check(matches(isDisplayed()))
    }

    @Test
    fun show_unknown_error_dialog() {
        scenario = ActivityScenario.launch(DetailsActivity::class.java)
        liveData.value = DetailsPresenter.ViewModel(
            showUnknownError = true
        )

        onView(withText(R.string.unknown_error)).check(matches(isDisplayed()))
    }

    @Test
    fun on_product_added_to_cart_show_message() {
        scenario = ActivityScenario.launch(DetailsActivity::class.java)

        scenario.onActivity {

            liveData.value = DetailsPresenter.ViewModel(
                productAddedToCart = true
            )

            verify(toastManager).show(it, R.string.product_added)
        }
    }

    @Test
    fun on_click_like_menu() {

        scenario = ActivityScenario.launch(DetailsActivity::class.java)

        onView(withId(R.id.menu_like)).perform(click())

        verify(presenter).onClickLike()
    }

    @Test
    fun icon_showed_when_product_is_liked() {

        scenario = ActivityScenario.launch(DetailsActivity::class.java)
        liveData.value = DetailsPresenter.ViewModel(liked = true)

        onView(withId(R.id.menu_like)).check(
            matches(DrawableMatchers.withMenuDrawable(R.drawable.ic_baseline_favorite_white_24))
        )
    }

    @Test
    fun icon_showed_when_product_is_not_liked() {

        scenario = ActivityScenario.launch(DetailsActivity::class.java)
        liveData.value = DetailsPresenter.ViewModel(liked = false)

        onView(withId(R.id.menu_like)).check(
            matches(DrawableMatchers.withMenuDrawable(R.drawable.ic_baseline_favorite_white_border_24))
        )
    }
}
