package com.rpandey.covid19tracker_india.ui.notifications.todaysnews

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.database.entity.NewsEntity
import com.squareup.picasso.Picasso

class TodaysNewsAdapter(
    private var items: List<NewsEntity>,
    private val clickListener: (NewsEntity, ImageView, TextView) -> Unit
) : RecyclerView.Adapter<TodaysNewsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todays_news, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.init(item)
    }

    override fun getItemCount(): Int = items.size

    fun updateData(items: List<NewsEntity>) {
        this.items = items
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val imageView: ImageView = view.findViewById(R.id.iv_news)
        private val headline: TextView = view.findViewById(R.id.tv_headlines)

        fun init(dataItem: NewsEntity) {
            Picasso
                .get()
                .load(dataItem.image)
                .into(imageView)
            headline.text = dataItem.headline

            itemView.setOnClickListener { clickListener(dataItem, imageView, headline) }
        }
    }
}