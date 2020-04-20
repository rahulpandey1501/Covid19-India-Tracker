package com.rpandey.covid19tracker_india.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository
import com.rpandey.covid19tracker_india.database.entity.StateEntity
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase
import com.rpandey.covid19tracker_india.databinding.ItemSelectStateBinding
import com.rpandey.covid19tracker_india.databinding.LayoutSelectStateBsBinding
import com.rpandey.covid19tracker_india.ui.BaseBottomSheetFragment
import com.rpandey.covid19tracker_india.util.getViewModel

class SelectStateBottomSheet : BaseBottomSheetFragment() {

    companion object {
        const val TAG = "SelectStateBottomSheet"
    }

    interface Callback {
        fun onSateSelected(stateEntity: StateEntity)
    }

    private lateinit var callback: Callback

    private val repository by lazy {
        CovidIndiaRepository(CovidDatabase.getInstance(requireContext()))
    }

    private val viewModel by lazy {
        getViewModel { SelectStateViewModel(repository) }
    }

    private lateinit var binding: LayoutSelectStateBsBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Callback)
            callback = context
        else if (parentFragment is Callback)
            callback = parentFragment as Callback
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutSelectStateBsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getStates().observe(viewLifecycleOwner, Observer {
            inflateStates(it)
        })
    }

    private fun inflateStates(states: List<StateEntity>) {
        binding.stateContainer.removeAllViews()
        states.forEach { state ->
            val itemBinding = ItemSelectStateBinding.inflate(LayoutInflater.from(requireContext()),
                binding.stateContainer, true)
            itemBinding.tvState.text = state.name
            itemBinding.root.setOnClickListener {
                callback.onSateSelected(state)
                dismissAllowingStateLoss()
            }
        }
    }
}