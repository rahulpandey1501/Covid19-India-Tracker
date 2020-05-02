package com.rpandey.covid19tracker_india.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rpandey.covid19tracker_india.database.dao.CombinedCasesModel
import com.rpandey.covid19tracker_india.databinding.ItemCombinedViewBinding
import com.rpandey.covid19tracker_india.util.Util

class StatesAdapter(var data: MutableList<CombinedCasesModel>, private val callback: (CombinedCasesModel) -> Unit): RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCombinedViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding) {
            callback(data[it])
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.init(data[position])
    }

    fun update(data: MutableList<CombinedCasesModel>) {
        this.data = data
        notifyDataSetChanged()
    }
}

class ViewHolder constructor(private val binding: ItemCombinedViewBinding, private val clickCallback: (Int) -> Unit) : RecyclerView.ViewHolder(binding.root) {

    fun init(data: CombinedCasesModel) {
        with(binding) {
            tvActive.text = Util.formatNumber(data.activeCases)

            tvConfirmed.text = Util.formatNumber(data.totalConfirmedCases)
            setDelta(tvConfirmedDelta, data.confirmedCases)

            tvRecovered.text = Util.formatNumber(data.totalRecoveredCases)
            setDelta(tvRecoveredDelta, data.recoveredCases)

            tvDeath.text = Util.formatNumber(data.totalDeceasedCases)
            setDelta(tvDeathDelta, data.deceasedCases)

            tvTitle.text = data.stateName

            itemView.setOnClickListener {
                clickCallback(adapterPosition)
            }
        }
    }

    private fun setDelta(textView: TextView, count: Int) {
        if (count > 0) {
            textView.visibility = View.VISIBLE
            textView.text = String.format("+%s", Util.formatNumber(count))
        } else {
            textView.visibility = View.INVISIBLE
        }
    }
}