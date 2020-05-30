package com.rpandey.covid19tracker_india.ui.common

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.databinding.ItemSelectorBinding
import com.rpandey.covid19tracker_india.ui.BaseBottomSheetFragment
import kotlinx.android.synthetic.main.layout_item_selector_bs.*

open class ItemSelectorBottomSheet<T>: BaseBottomSheetFragment() {

    data class Item<T>(val name: String, val identifier: T)

    interface Callback<T> {
        fun onItemSelected(tag: String, item: Item<T>)
    }

    private var callback: Callback<T>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Callback<*>)
            callback = context as Callback<T>
        else if (parentFragment is Callback<*>)
            callback = parentFragment as Callback<T>
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_item_selector_bs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title.text = requireArguments().getString(KEY_TITLE)
        iv_close.setOnClickListener { dismissAllowingStateLoss() }
        val items: List<Item<T>> = getItems()
        inflateItems(items)
    }

    open fun getItems():List<Item<T>> {
        return Gson().fromJson(requireArguments().getString(KEY_ITEMS), object: TypeToken<List<Item<T>>>(){}.type)
    }

    fun inflateItems(items: List<Item<T>>) {
        container.removeAllViews()
        items.forEach { item ->
            val itemBinding = ItemSelectorBinding.inflate(LayoutInflater.from(requireContext()), container, true)
            itemBinding.title.text = item.name
            itemBinding.root.setOnClickListener {
                callback?.onItemSelected(tag!!, item)
                dismissAllowingStateLoss()
            }
        }
    }

    companion object {
        const val KEY_TITLE = "KEY_TITLE"
        const val KEY_ITEMS = "KEY_ITEMS"

        fun <T>newInstance(title: String, items: List<Item<T>>): ItemSelectorBottomSheet<T> {
            return ItemSelectorBottomSheet<T>().apply { arguments = Bundle().apply {
                putString(KEY_TITLE, title)
                putString(KEY_ITEMS, Gson().toJson(items))
            } }
        }
    }
}