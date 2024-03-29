package com.rpandey.covid19tracker_india.ui.statedetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rpandey.covid19tracker_india.database.entity.DistrictEntity
import com.rpandey.covid19tracker_india.databinding.ItemCombinedViewDistrictBinding
import com.rpandey.covid19tracker_india.util.Util

class DistrictListAdapter(var data: MutableList<DistrictEntity>, private val clickCallback: (DistrictEntity) -> Unit) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCombinedViewDistrictBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding) {
            clickCallback(data[it])
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.init(data[position])
    }

    fun update(data: MutableList<DistrictEntity>) {
        this.data = data
        notifyDataSetChanged()
    }
}

class ViewHolder(val binding: ItemCombinedViewDistrictBinding, val callback: (Int) -> Unit): RecyclerView.ViewHolder(binding.root) {

    fun init(district: DistrictEntity) {
        with(binding) {
            tvTitle.text = district.district

            tvConfirmed.text = Util.formatNumber(district.totalConfirmed)
            setDelta(tvConfirmedDelta, district.confirmed)

            tvRecovered.text = Util.formatNumber(district.totalRecovered)
            setDelta(tvRecoveredDelta, district.recovered)

            tvDeath.text = Util.formatNumber(district.totalDeceased)
            setDelta(tvDeathDelta, district.deceased)

            tvActive.text = Util.formatNumber(district.getActive())

            val context = binding.root.context
            zoneIndicator.setBackgroundColor(Util.getZoneColor(context, district.zone))

            itemView.setOnClickListener {
                callback(adapterPosition)
            }
        }
    }

    private fun setDelta(textView: TextView, count: Int) {
        if (count > 0) {
            textView.visibility = View.VISIBLE
            textView.text = String.format("+%s", Util.formatNumber(count))
        } else {
            textView.visibility = View.GONE
        }
    }
}