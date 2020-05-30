package com.rpandey.covid19tracker_india.ui.notifications.todaysnews.details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Explode
import android.view.Window
import android.view.WindowManager
import androidx.core.view.ViewCompat
import com.google.gson.Gson
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.ui.BaseActivity
import com.rpandey.covid19tracker_india.ui.notifications.todaysnews.TodaysNewsViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_news_details.*

class NewsDetailsActivity : BaseActivity() {

    companion object {
        const val KEY_DATA = "data"
        const val VIEW_IMAGE = "newsDetails:ImageView"
        const val VIEW_HEADLINE = "newsDetails:Headline"
    }

    override fun getScreenName(): String {
        return "NewsDetailsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_details)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(android.R.color.background_dark)
        ViewCompat.setTransitionName(iv_news, VIEW_IMAGE)
        ViewCompat.setTransitionName(headline, VIEW_HEADLINE)
        readBundle()
    }

    private fun readBundle() {
        val data = Gson().fromJson(intent.getStringExtra(KEY_DATA), TodaysNewsViewModel.DataItem::class.java)
        headline.text = data.headline
        summary.text = data.summary
        Picasso.get()
            .load(data.imageLink)
            .into(iv_news)
    }
}