package com.rpandey.covid19tracker_india.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.rpandey.covid19tracker_india.database.dao.CombinedCasesModel
import com.rpandey.covid19tracker_india.database.entity.DistrictEntity
import com.rpandey.covid19tracker_india.databinding.ItemCombinedViewDistrictBinding
import com.rpandey.covid19tracker_india.databinding.ItemDistrictCasesMinimalBinding
import com.rpandey.covid19tracker_india.util.Util

class SearchListAdapter(
    private val viewType: String, private var districts: List<DistrictEntity>, var states: List<CombinedCasesModel>,
    private val districtCallback: (DistrictEntity) -> Unit, private val stateCallback: (CombinedCasesModel) -> Unit) : RecyclerView.Adapter<ViewHolder<out ViewDataBinding>>() {

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ViewHolder<out ViewDataBinding> {

        val binding = if (viewType == SearchActivity.DISTRICT_SEARCH_VIEW) {
            ItemDistrictCasesMinimalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        } else {
            ItemCombinedViewDistrictBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        }

        return ViewHolder(binding) {
            val data = getBindingData(it)
            if (data is DistrictEntity) {
                districtCallback(data)
            } else if (data is CombinedCasesModel) {
                stateCallback(data)
            }
        }
    }

    override fun getItemCount(): Int {
        return districts.size + states.size
    }

    override fun onBindViewHolder(holder: ViewHolder<out ViewDataBinding>, position: Int) {
        val data = getBindingData(position)
        if (data is DistrictEntity) {
            holder.init(data)
        } else if (data is CombinedCasesModel) {
            holder.init(data)
        }
    }

    fun update(districts: List<DistrictEntity>, states: List<CombinedCasesModel>) {
        this.districts = districts
        this.states = states
        notifyDataSetChanged()
    }

    // inflate states first then districts
    private fun getBindingData(position: Int): Any {
        return if (states.size > position) {
            states[position]

        } else {
            districts[position - states.size]
        }
    }
}

class ViewHolder<B: ViewDataBinding>(val binding: B, private val callback: (Int) -> Unit): RecyclerView.ViewHolder(binding.root) {

    fun init(district: DistrictEntity) {
        if (binding is ItemCombinedViewDistrictBinding) {
            with(binding as ItemCombinedViewDistrictBinding) {
                tvTitle.text = district.district

                tvConfirmed.text = Util.formatNumber(district.totalConfirmed)
                setDelta(tvConfirmedDelta, district.confirmed)

                tvRecovered.text = Util.formatNumber(district.totalRecovered)
                setDelta(tvRecoveredDelta, district.recovered)

                tvDeath.text = Util.formatNumber(district.totalDeceased)
                setDelta(tvDeathDelta, district.deceased)

                tvActive.text = Util.formatNumber(district.getActive())

                val context = binding.root.context
                zoneIndicator.visibility = View.VISIBLE
                zoneIndicator.setBackgroundColor(Util.getZoneColor(context, district.zone))

                itemView.setOnClickListener {
                    callback(adapterPosition)
                }
            }

        } else if (binding is ItemDistrictCasesMinimalBinding) {
            binding.tvTitle.text = district.district
            binding.tvCount.text = Util.formatNumber(district.getActive())
            binding.zoneIndicator.setBackgroundColor(Util.getZoneColor(binding.tvCount.context, district.zone))
            binding.root.setOnClickListener {
                callback(adapterPosition)
            }
        }
    }

    fun init(state: CombinedCasesModel) {
        with(binding as ItemCombinedViewDistrictBinding) {
            tvTitle.text = state.stateName

            tvConfirmed.text = Util.formatNumber(state.totalConfirmedCases)
            setDelta(tvConfirmedDelta, state.confirmedCases)

            tvRecovered.text = Util.formatNumber(state.totalRecoveredCases)
            setDelta(tvRecoveredDelta, state.recoveredCases)

            tvDeath.text = Util.formatNumber(state.totalDeceasedCases)
            setDelta(tvDeathDelta, state.deceasedCases)

            tvActive.text = Util.formatNumber(state.activeCases)

            zoneIndicator.visibility = View.GONE

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