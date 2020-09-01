package com.alavpa.bsproducts.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alavpa.bsproducts.R
import com.alavpa.bsproducts.presentation.main.MainPresenter
import com.alavpa.bsproducts.utils.loader.ImageLoader
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val imageLoader: ImageLoader by inject()
    private val recyclerView: RecyclerView by lazy { findViewById(R.id.rv_products) }
    private val pullToRefresh: SwipeRefreshLayout by lazy { findViewById(R.id.pull_to_refresh) }

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

        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = MainAdapter(this, imageLoader)

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

        adapter?.load(viewModel.items)
    }
}
