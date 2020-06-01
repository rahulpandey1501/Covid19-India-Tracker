package com.rpandey.covid19tracker_india.ui.caseshistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.util.Util
import kotlinx.android.synthetic.main.item_history_cases.view.*
import kotlin.math.absoluteValue

class HistoryCasesAdapter(private var items: List<HistoryCasesViewModel.DataItem>) :
    RecyclerView.Adapter<HistoryCasesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_history_cases, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.get(position)
        holder.init(item)
    }

    fun update(items: List<HistoryCasesViewModel.DataItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun init(item: HistoryCasesViewModel.DataItem) {
            val dailyChangesEntity = item.entity
            itemView.title.text = dailyChangesEntity.date
            itemView.confirm_count.text = Util.formatNumber(dailyChangesEntity.confirmed)
            itemView.recover_count.text = Util.formatNumber(dailyChangesEntity.recovered)
            itemView.death_count.text = Util.formatNumber(dailyChangesEntity.deceased)

            itemView.confirm_count_delta.text = Util.formatNumber(item.deltaConfirm.absoluteValue)
            itemView.recover_count_delta.text = Util.formatNumber(item.deltaRecover.absoluteValue)
            itemView.death_count_delta.text = Util.formatNumber(item.deltaDeath.absoluteValue)

            itemView.arrow_confirm.rotation = if (item.deltaConfirm < 0) 180f else 0f
            itemView.arrow_recover.rotation = if (item.deltaConfirm < 0) 180f else 0f
            itemView.arrow_death.rotation = if (item.deltaConfirm < 0) 180f else 0f
        }
    }
}