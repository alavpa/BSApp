package com.alavpa.bsproducts.details

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.alavpa.bsproducts.R
import com.alavpa.bsproducts.utils.loader.ImageLoader
import com.alavpa.bsproducts.utils.navigation.BSNavigation
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailsActivity : AppCompatActivity() {

    private val navigation: BSNavigation by inject()
    private val imageLoader: ImageLoader by inject()

    private val presenter: DetailsPresenter by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        navigation.attach(this)
        presenter.attachNavigation(navigation)

        presenter.renderLiveData.observe(this, Observer(::render))
    }

    override fun onResume() {
        super.onResume()
        presenter.load()
    }

    private fun render(viewModel: DetailsPresenter.ViewModel) {

    }

    override fun onDestroy() {
        super.onDestroy()
        navigation.detach()
        presenter.detachNavigation()
        presenter.destroy()
    }
}
