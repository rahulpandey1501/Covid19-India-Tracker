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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ItemSelectorBottomSheet: BaseBottomSheetFragment() {

    data class Item(val name: String, val identifier: String)

    interface Callback {
        fun onItemSelected(tag: String, item: Item)
    }

    private var callback: Callback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Callback)
            callback = context
        else if (parentFragment is Callback)
            callback = parentFragment as Callback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_item_selector_bs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title.text = requireArguments().getString(KEY_TITLE)
        CoroutineScope(Dispatchers.IO).launch {
            val items: List<Item> = Gson().fromJson(requireArguments().getString(KEY_ITEMS), object: TypeToken<List<Item>>(){}.type)
            withContext(Dispatchers.Main) {
                inflateItems(items)
            }
        }
    }

    private fun inflateItems(items: List<Item>) {
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
        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_ITEMS = "KEY_ITEMS"

        fun newInstance(title: String, items: List<Item>): ItemSelectorBottomSheet {
            return ItemSelectorBottomSheet().apply { arguments = Bundle().apply {
                putString(KEY_TITLE, title)
                putString(KEY_ITEMS, Gson().toJson(items))
            } }
        }
    }
}