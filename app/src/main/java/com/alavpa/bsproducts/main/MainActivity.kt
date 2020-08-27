package com.alavpa.bsproducts.main

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alavpa.bsproducts.R
import com.alavpa.bsproducts.presentation.main.MainPresenter
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val loader: View by lazy { findViewById(R.id.view_loader) }
    private val recyclerView: RecyclerView by lazy { findViewById(R.id.rv_products) }
    private lateinit var gridLayoutManager: GridLayoutManager

    private val presenter: MainPresenter by inject()
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

        presenter.renderLiveData.observe(this, Observer(::render))
    }

    override fun onResume() {
        super.onResume()
        presenter.load()
    }

    private fun render(viewModel: MainPresenter.ViewModel) {
        loader.visibility = if (viewModel.isLoading) VISIBLE else GONE
    }
}
