package com.alavpa.bsproducts.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alavpa.bsproducts.R
import com.alavpa.bsproducts.presentation.main.MainPresenter
import com.alavpa.bsproducts.utils.dialog.ServerDialog
import com.alavpa.bsproducts.utils.dialog.UnknownErrorDialog
import com.alavpa.bsproducts.utils.loader.ImageLoader
import com.alavpa.bsproducts.utils.navigation.BSNavigation
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val navigation: BSNavigation by inject()
    private val imageLoader: ImageLoader by inject()
    private val recyclerView: RecyclerView by lazy { findViewById(R.id.rv_products) }
    private val pullToRefresh: SwipeRefreshLayout by lazy { findViewById(R.id.pull_to_refresh) }

    private var serverDialog: ServerDialog? = null

    private lateinit var gridLayoutManager: GridLayoutManager

    private val presenter: MainPresenter by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gridLayoutManager = GridLayoutManager(
            this,
            2,
            GridLayoutManager.VERTICAL,
            false
        )

        navigation.attach(this)
        presenter.attachNavigation(navigation)

        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = MainAdapter(
            this,
            imageLoader,
            presenter::clickOn
        )

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0) {
                    val visibleItemCount = gridLayoutManager.childCount
                    val totalItemCount = gridLayoutManager.itemCount
                    val pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition()

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        presenter.next()
                    }
                }
            }
        })

        pullToRefresh.setOnRefreshListener {
            presenter.load()
        }

        presenter.renderLiveData.observe(this, Observer(::render))
    }

    override fun onResume() {
        super.onResume()
        presenter.load()
    }

    private fun render(viewModel: MainPresenter.ViewModel) {

        pullToRefresh.isRefreshing = viewModel.isLoading
        val adapter = recyclerView.adapter as? MainAdapter

        adapter?.load(viewModel.items, viewModel.clear)

        if (viewModel.showServerException.first) {
            showServerDialog(viewModel.showServerException.second)
        }

        if (viewModel.showUnknownError) {
            showUnknownError()
        }
    }

    private fun showServerDialog(message: String) {
        if (serverDialog == null) {
            serverDialog = ServerDialog.newInstance(
                message,
                object : ServerDialog.ServerDialogListener {
                    override fun onOk() {
                        serverDialog = null
                        presenter.onCloseServerException()
                    }
                }
            ).apply { isCancelable = false }

            serverDialog?.show(supportFragmentManager, "ServerDialog")
        }
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

    override fun onDestroy() {
        super.onDestroy()
        navigation.detach()
        presenter.detachNavigation()
        presenter.destroy()
    }
}
