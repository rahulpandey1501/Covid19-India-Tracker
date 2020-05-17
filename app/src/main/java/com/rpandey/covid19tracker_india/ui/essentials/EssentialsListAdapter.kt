package com.rpandey.covid19tracker_india.ui.essentials

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rpandey.covid19tracker_india.database.entity.ResourcesEntity
import com.rpandey.covid19tracker_india.databinding.ItemEssentialBinding
import com.rpandey.covid19tracker_india.util.Util

class EssentialsListAdapter(private var items: List<ResourcesEntity>): RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemEssentialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position != -1 && position < items.size) {
            holder.init(items[position])
        }
    }

    fun update(items: List<ResourcesEntity>) {
        this.items = items
        notifyDataSetChanged()
    }
}

class ViewHolder(val binding: ItemEssentialBinding): RecyclerView.ViewHolder(binding.root) {

    fun init(resourcesEntity: ResourcesEntity) {
        binding.title.text = resourcesEntity.organisation
        binding.description.text = resourcesEntity.description

        val phoneNumbers = resourcesEntity.phoneNumber.split(",")
        binding.phoneNo1.visibility = View.GONE
        binding.phoneNo2.visibility = View.GONE

        if (phoneNumbers.size > 0 && phoneNumbers[0].isNotEmpty()) {
            binding.phoneNo1.text = phoneNumbers[0]
            binding.phoneNo1.visibility = View.VISIBLE
            binding.phoneNo1.setOnClickListener { Util.openCallIntent(it.context, phoneNumbers[0]) }
        }
        if (phoneNumbers.size > 1) {
            binding.phoneNo2.text = phoneNumbers[1]
            binding.phoneNo2.visibility = View.VISIBLE
            binding.phoneNo2.setOnClickListener { Util.openCallIntent(it.context, phoneNumbers[1]) }
        }

        binding.root.setOnClickListener { Util.openWebUrl(it.context, resourcesEntity.contact) }
    }
}