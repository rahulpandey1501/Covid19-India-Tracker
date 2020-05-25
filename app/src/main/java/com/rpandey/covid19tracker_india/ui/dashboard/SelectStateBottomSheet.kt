package com.rpandey.covid19tracker_india.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.rpandey.covid19tracker_india.CovidApplication
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository
import com.rpandey.covid19tracker_india.database.entity.StateEntity
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase
import com.rpandey.covid19tracker_india.ui.common.ItemSelectorBottomSheet
import com.rpandey.covid19tracker_india.util.getViewModel

class SelectStateBottomSheet : ItemSelectorBottomSheet<StateEntity>() {

    companion object {
        const val TAG = "SelectStateBottomSheet"
        fun newInstance(): SelectStateBottomSheet {
            return SelectStateBottomSheet().apply { arguments = Bundle().apply {
                putString(KEY_TITLE, CovidApplication.INSTANCE.getString(R.string.select_state))
                putString(KEY_ITEMS, Gson().toJson(emptyList<Item<StateEntity>>()))
            } }
        }
    }

    interface Callback: ItemSelectorBottomSheet.Callback<StateEntity> {
        fun onSateSelected(stateEntity: StateEntity)
        override fun onItemSelected(tag: String, item: Item<StateEntity>) {
            onSateSelected(item.identifier)
        }
    }

    private lateinit var callback: Callback

    private val repository by lazy {
        CovidIndiaRepository(CovidDatabase.getInstance())
    }

    private val viewModel by lazy {
        getViewModel { SelectStateViewModel(repository) }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Callback)
            callback = context
        else if (parentFragment is Callback)
            callback = parentFragment as Callback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        viewModel.getStates().observe(viewLifecycleOwner, Observer {
            inflateItems(it)
        })
        return view
    }
}