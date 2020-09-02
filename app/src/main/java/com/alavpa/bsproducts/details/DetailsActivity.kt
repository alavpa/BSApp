package com.alavpa.bsproducts.details

import android.animation.Animator
import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.view.GestureDetector
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.alavpa.bsproducts.R
import com.alavpa.bsproducts.presentation.details.DetailsPresenter
import com.alavpa.bsproducts.utils.dialog.ServerDialog
import com.alavpa.bsproducts.utils.dialog.ToastManager
import com.alavpa.bsproducts.utils.dialog.UnknownErrorDialog
import com.alavpa.bsproducts.utils.loader.ImageLoader
import com.alavpa.bsproducts.utils.navigation.BSNavigation
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailsActivity : AppCompatActivity() {

    private val navigation: BSNavigation by inject()
    private val imageLoader: ImageLoader by inject()
    private val toastManager: ToastManager by inject()

    private val presenter: DetailsPresenter by viewModel()

    private val image: ImageView by lazy { findViewById(R.id.image) }
    private val tvBrand: TextView by lazy { findViewById(R.id.tv_brand) }
    private val tvDescription: TextView by lazy { findViewById(R.id.tv_description) }
    private val tvPrice: TextView by lazy { findViewById(R.id.tv_price) }
    private val tvDiscount: TextView by lazy { findViewById(R.id.tv_discount) }
    private val toolbar: Toolbar by lazy { findViewById(R.id.toolbar) }
    private val btnAdd: FloatingActionButton by lazy { findViewById(R.id.btn_add) }
    private val pullToRefresh: SwipeRefreshLayout by lazy { findViewById(R.id.pull_to_refresh) }
    private val lottieAnimation: LottieAnimationView by lazy { findViewById(R.id.animation) }

    private var isLiked = false
    private var gestureDetector: GestureDetector? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        navigation.attach(this)
        presenter.attachNavigation(navigation)

        btnAdd.setOnClickListener {
            presenter.onAddToCart()
        }

        pullToRefresh.setOnRefreshListener {
            presenter.load(intent.getLongExtra(BSNavigation.EXTRA_ID, 0))
        }

        gestureDetector = GestureDetector(
            this,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent?): Boolean {
                    presenter.onClickLike()
                    return true
                }
            }
        )

        image.setOnTouchListener { v, event ->
            gestureDetector?.onTouchEvent(event)
            when (event.action) {
                MotionEvent.ACTION_UP -> v.performClick()
            }
            true
        }

        lottieAnimation.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                // no-op
            }

            override fun onAnimationEnd(animation: Animator?) {
                lottieAnimation.visibility = GONE
                lottieAnimation.progress = 0f
            }

            override fun onAnimationCancel(animation: Animator?) {
                lottieAnimation.progress = 0f
            }

            override fun onAnimationRepeat(animation: Animator?) {
                // no-op
            }
        })

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tvPrice.paintFlags = tvPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        presenter.renderLiveData.observe(this, Observer(::render))
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector?.onTouchEvent(event)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return menu?.let {
            it.findItem(R.id.menu_like).icon = if (isLiked)
                ContextCompat.getDrawable(this, R.drawable.ic_baseline_favorite_white_24)
            else ContextCompat.getDrawable(this, R.drawable.ic_baseline_favorite_white_border_24)
            true
        } ?: super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                presenter.close()
                true
            }
            R.id.menu_like -> {
                presenter.onClickLike()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.load(intent.getLongExtra(BSNavigation.EXTRA_ID, 0))
    }

    private fun render(viewModel: DetailsPresenter.ViewModel) {
        pullToRefresh.isRefreshing = viewModel.isLoading
        imageLoader.load(this, viewModel.image, image)
        supportActionBar?.title = viewModel.title
        tvBrand.text = viewModel.brand
        tvDescription.text = viewModel.description
        tvPrice.text = viewModel.price
        tvDiscount.text = viewModel.priceWithDiscount
        if (isLiked != viewModel.liked) {
            isLiked = viewModel.liked
            invalidateOptionsMenu()
        }

        if (viewModel.showNoStockError) {
            showNoStockError()
        }

        if (viewModel.showFeatureNotImplementedError) {
            showFeatureNotImplementedError()
        }

        if (viewModel.showUnknownError) {
            showUnknownError()
        }

        if (viewModel.showServerException.first) {
            showServerDialog(viewModel.showServerException.second)
        }

        if (viewModel.productAddedToCart) {
            toastManager.show(this, R.string.product_added)
        }

        if (viewModel.showAnimation) {
            lottieAnimation.visibility = VISIBLE
            lottieAnimation.playAnimation()
        }
    }

    private fun showNoStockError() {
        NoStockDialog.newInstance(
            object : NoStockDialog.NoStockDialogListener {
                override fun onNotify() {
                    presenter.onNotify()
                }

                override fun onCancel() {
                    presenter.onCancelNoStockDialog()
                }
            }
        ).apply { isCancelable = false }
            .show(supportFragmentManager, "NoStockDialog")
    }

    private fun showFeatureNotImplementedError() {
        FeatureNotImplementedDialog.newInstance(
            object : FeatureNotImplementedDialog.FeatureNotImplementedDialogListener {
                override fun onOk() {
                    presenter.onCloseFeatureNotImplementedDialog()
                }
            }
        ).apply { isCancelable = false }
            .show(supportFragmentManager, "FeatureNotImplementedDialog")
    }

    private fun showUnknownError() {
        UnknownErrorDialog.newInstance(
            object : UnknownErrorDialog.UnknownErrorDialogListener {
                override fun onOk() {
                    presenter.onCloseUnknownError()
                }
            }
        ).apply { isCancelable = false }
            .show(supportFragmentManager, "UnknownErrorDialog")
    }

    private fun showServerDialog(message: String) {
        ServerDialog.newInstance(
            message,
            object : ServerDialog.ServerDialogListener {
                override fun onOk() {
                    presenter.onCloseServerException()
                }
            }
        ).apply { isCancelable = false }
            .show(supportFragmentManager, "ServerDialog")
    }

    override fun onDestroy() {
        super.onDestroy()
        navigation.detach()
        presenter.detachNavigation()
        presenter.destroy()
    }
}
