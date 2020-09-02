package com.alavpa.bsproducts.details

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alavpa.bsproducts.R
import com.alavpa.bsproducts.presentation.details.DetailsPresenter
import com.alavpa.bsproducts.utils.dialog.ServerDialog
import com.alavpa.bsproducts.utils.dialog.UnknownErrorDialog
import com.alavpa.bsproducts.utils.loader.ImageLoader
import com.alavpa.bsproducts.utils.navigation.BSNavigation
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailsActivity : AppCompatActivity() {

    private val navigation: BSNavigation by inject()
    private val imageLoader: ImageLoader by inject()

    private val presenter: DetailsPresenter by viewModel()

    private val image: ImageView by lazy { findViewById(R.id.image) }
    private val tvBrand: TextView by lazy { findViewById(R.id.tv_brand) }
    private val tvDescription: TextView by lazy { findViewById(R.id.tv_description) }
    private val tvPrice: TextView by lazy { findViewById(R.id.tv_price) }
    private val tvDiscount: TextView by lazy { findViewById(R.id.tv_discount) }
    private val toolbar: Toolbar by lazy { findViewById(R.id.toolbar) }
    private val btnAdd: ExtendedFloatingActionButton by lazy { findViewById(R.id.btn_add) }
    private val pullToRefresh: SwipeRefreshLayout by lazy { findViewById(R.id.pull_to_refresh) }

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

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        presenter.renderLiveData.observe(this, Observer(::render))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                presenter.close()
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
            Toast.makeText(this, R.string.product_added, Toast.LENGTH_SHORT).show()
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
        ).show(supportFragmentManager, "NoStockDialog")
    }

    private fun showFeatureNotImplementedError() {
        FeatureNotImplementedDialog.newInstance(
            object : FeatureNotImplementedDialog.FeatureNotImplementedDialogListener {
                override fun onOk() {
                    presenter.onCloseFeatureNotImplementedDialog()
                }
            }
        ).show(supportFragmentManager, "FeatureNotImplementedDialog")
    }

    private fun showUnknownError() {
        UnknownErrorDialog.newInstance(
            object : UnknownErrorDialog.UnknownErrorDialogListener {
                override fun onOk() {
                    presenter.onCloseUnknownError()
                }
            }
        ).show(supportFragmentManager, "UnknownErrorDialog")
    }

    private fun showServerDialog(message: String) {
        ServerDialog.newInstance(
            message,
            object : ServerDialog.ServerDialogListener {
                override fun onOk() {
                    presenter.onCloseServerException()
                }
            }
        ).show(supportFragmentManager, "ServerDialog")
    }

    override fun onDestroy() {
        super.onDestroy()
        navigation.detach()
        presenter.detachNavigation()
        presenter.destroy()
    }
}
