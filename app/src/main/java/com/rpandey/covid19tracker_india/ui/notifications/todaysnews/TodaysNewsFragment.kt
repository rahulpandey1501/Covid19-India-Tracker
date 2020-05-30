package com.rpandey.covid19tracker_india.ui.notifications.todaysnews

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import com.google.gson.Gson
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.network.APIProvider
import com.rpandey.covid19tracker_india.ui.BaseFragment
import com.rpandey.covid19tracker_india.ui.notifications.todaysnews.details.NewsDetailsActivity
import com.rpandey.covid19tracker_india.util.getViewModel
import com.rpandey.covid19tracker_india.util.observe
import kotlinx.android.synthetic.main.fragment_todays_news.*
import kotlinx.android.synthetic.main.fragment_todays_news.view.*


/**
 * A fragment representing a list of Items.
 */
class TodaysNewsFragment : BaseFragment() {

    companion object {
        const val TAG = "TodaysNewsFragment"
    }

    private val viewModel by lazy {
        getViewModel {
            TodaysNewsViewModel(
                APIProvider.getInstance()
            )
        }
    }

    private val adapter = TodaysNewsAdapter(mutableListOf()) { data, image, headline ->
        onItemClicked(data, image, headline)
    }

    override fun setToolbarTitle(): String {
        return getString(R.string.page_title_news)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_todays_news, container, false)
        view.rv_news.adapter = adapter
        return view
    }

    override fun observeLiveData() {

        viewModel.items.observe(this) {
            if (it.isEmpty()) {
                showError()

            } else {
                loadData(it)
            }
        }

        retry.setOnClickListener { onRetryClicked() }
    }

    private fun onItemClicked(dataItem: TodaysNewsViewModel.DataItem, image: View, headline: View) {
        val p1 = androidx.core.util.Pair.create(image, NewsDetailsActivity.VIEW_IMAGE)
//        val p2 = androidx.core.util.Pair.create(headline as View, NewsDetailsActivity.VIEW_HEADLINE)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), p1)
        val intent = Intent(activity, NewsDetailsActivity::class.java).apply {
            putExtra(NewsDetailsActivity.KEY_DATA, Gson().toJson(dataItem))
            putExtra(NewsDetailsActivity.KEY_SOURCE, viewModel.sourceNews.value)
        }
        startActivity(intent, options.toBundle())
    }

    private fun onRetryClicked() {
        viewModel.fetchData()
        loader.visibility = View.VISIBLE
        error.visibility = View.GONE
        rv_news.visibility = View.GONE
    }

    private fun loadData(items: List<TodaysNewsViewModel.DataItem>) {
        loader.visibility = View.GONE
        error.visibility = View.GONE
        rv_news.visibility = View.VISIBLE
        adapter.updateData(items)
    }

    private fun showError() {
        loader.visibility = View.GONE
        error.visibility = View.VISIBLE
        rv_news.visibility = View.GONE
    }
}