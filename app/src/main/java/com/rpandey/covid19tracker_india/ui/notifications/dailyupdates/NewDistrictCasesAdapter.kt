package com.rpandey.covid19tracker_india.ui.notifications.dailyupdates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rpandey.covid19tracker_india.data.Constants
import com.rpandey.covid19tracker_india.data.model.IndianStates
import com.rpandey.covid19tracker_india.database.entity.DistrictEntity
import com.rpandey.covid19tracker_india.databinding.ItemDistrictCasesMinimalBinding
import com.rpandey.covid19tracker_india.util.Util

class NewDistrictCasesAdapter(var data: MutableList<DistrictEntity>, private val clickCallback: (DistrictEntity) -> Unit) : RecyclerView.Adapter<NewDistrictCasesAdapter.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDistrictCasesMinimalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    inner class ViewHolder(val binding: ItemDistrictCasesMinimalBinding, val callback: (Int) -> Unit): RecyclerView.ViewHolder(binding.root) {

        fun init(district: DistrictEntity) {
            with(binding) {
                tvTitle.text = district.district
                tvCount.text = Util.formatNumber(district.confirmed)
                itemView.setOnClickListener {
                    callback(adapterPosition)
                }
                if (district.district.equals(Constants.UNKNOWN, true)) {
                    val state = IndianStates.fromName(district.stateName)
                    val stateText = if (state != IndianStates.UN) state.stateCode else district.stateName
                    tvTitle.text = "${district.district} ($stateText)"
                }
            }
        }
    }
}