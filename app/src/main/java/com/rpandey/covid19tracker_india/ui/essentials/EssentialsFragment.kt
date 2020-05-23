package com.rpandey.covid19tracker_india.ui.essentials

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.data.Constants
import com.rpandey.covid19tracker_india.database.entity.ResourcesEntity
import com.rpandey.covid19tracker_india.databinding.FragmentEssentialsBinding
import com.rpandey.covid19tracker_india.ui.BaseFragment
import com.rpandey.covid19tracker_india.ui.common.ItemSelectorBottomSheet
import com.rpandey.covid19tracker_india.util.*
import kotlinx.android.synthetic.main.fragment_essentials.*

class EssentialsFragment : BaseFragment(), ItemSelectorBottomSheet.Callback {

    override fun getScreenName() = "EssentialsFragment"

    companion object {
        const val TAG = "EssentialsFragment"
        const val KEY_STATE = "KEY_STATE"
        const val KEY_DISTRICT = "KEY_DISTRICT"
        const val KEY_STRICT_VIEW = "KEY_STRICT_VIEW"

        fun newInstance(state: String, district: String, onlyCategoryView: Boolean): EssentialsFragment {
            return EssentialsFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_STATE, state)
                    putString(KEY_DISTRICT, district)
                    putBoolean(KEY_STRICT_VIEW, onlyCategoryView)
                }
            }
        }
    }

    private val stateSelectionTag = "stateSelectionTag"
    private val districtSelectionTag = "districtSelectionTag"
    private val categorySelectionTag = "categorySelectionTag"

    private var selectionChanged = false
    private var selectedState: String? = null
    private var selectedDistrict: String? = null
    private var selectedCategory: String? = null

    private lateinit var adapter: EssentialsListAdapter
    private lateinit var binding: FragmentEssentialsBinding

    private val viewModel by lazy {
        getViewModel { EssentialsViewModel(repository) }
    }

    override fun setToolbarTitle(): String {
        return getString(R.string.title_essentials)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEssentialsBinding.inflate(inflater, container, false)
        adapter = EssentialsListAdapter(emptyList())
        binding.recyclerView.adapter = adapter
        if (arguments?.getBoolean(KEY_STRICT_VIEW) == true) {
            binding.stateDistrictView.visibility = View.GONE
            binding.nearbyEssentials.visibility = View.GONE
            binding.selectLocationTitle.visibility = View.GONE
        }
        return binding.root
    }

    override fun observeLiveData() {
        with(viewModel) {

            stateLD.observe(this@EssentialsFragment) { data ->
                val initialState = arguments?.getString(KEY_STATE)
                if (!selectionChanged && data.contains(initialState)) {
                    initialState?.let { selectState(it) }
                }
            }

            districtLD.observe(this@EssentialsFragment) { data ->
                val initialDistrict = arguments?.getString(KEY_DISTRICT)
                if (!selectionChanged && data.contains(initialDistrict)) {
                    initialDistrict?.let { selectDistrict(it) }
                } else {
                    district_view.performClick()
                }
            }

            categoriesLD.observe(this@EssentialsFragment) { data ->
                data.firstOrNull()?.let { selectCategory(it) }
            }

            resourcesLD.observe(this@EssentialsFragment) {
                fillResources(it)
            }
        }
        setupListeners()
    }

    private fun setupListeners() {

        state_view.setOnClickListener {
            val items = viewModel.stateLD.value?.map { ItemSelectorBottomSheet.Item(it, it) }
            if (items.isNullOrEmpty()) {
                requireContext().showToast("Data not found, please refresh")
                return@setOnClickListener
            }
            showDialog(stateSelectionTag, getString(R.string.select_state), items)
            selectionChanged = true
        }

        district_view.setOnClickListener {
            val items = viewModel.districtLD.value?.map { ItemSelectorBottomSheet.Item(it, it) }
            items?.let { showDialog(districtSelectionTag, getString(R.string.select_district), items) }
            selectionChanged = true
        }

        service_view.setOnClickListener {
            val items = viewModel.categoriesLD.value?.map { ItemSelectorBottomSheet.Item(it, it) }
            items?.let { showDialog(categorySelectionTag, getString(R.string.select_essential), items) }
            selectionChanged = true
        }

        nearby_essentials.setOnClickListener {
            val url = Util.getConfig()?.nearByEssentialsUrl ?: Constants.DEFAULT_NEARBY_ESSENTIALS_URL
            Util.openWebUrl(requireContext(), url, null, true)
        }
    }

    override fun onItemSelected(tag: String, item: ItemSelectorBottomSheet.Item) {
        when (tag) {
            stateSelectionTag -> {
                selectState(item.identifier)

            }
            districtSelectionTag -> {
                selectDistrict(item.identifier)

            }
            categorySelectionTag -> {
                selectCategory(item.identifier)
            }
        }
    }

    private fun selectState(state: String) {
        if (selectedState == state) return

        selectedState = state
        select_state.text = state
        viewModel.onStateSelected(state)
        resetDistrict()
        resetCategory()
    }

    private fun selectDistrict(district: String) {
        if (selectedState == null) return

        selectedDistrict = district
        select_district.text = district
        if (selectedState != null) {
            viewModel.onDistrictSelected(selectedState!!, district)
        }
    }

    private fun selectCategory(category: String) {
        if (selectedDistrict == null || selectedDistrict == null) return

        selectedCategory = category
        select_category.text = category
        if (selectedState != null && selectedDistrict != null) {
            viewModel.onCategorySelected(selectedState!!, selectedDistrict!!, category)
        }
    }

    private fun resetDistrict() {
        selectedDistrict = null
        select_district.text = getString(R.string.select_district)
    }

    private fun resetCategory() {
        selectedCategory = null
        fillResources(emptyList())
    }

    private fun fillResources(resources: List<ResourcesEntity>) {
        service_view.visibility = if (resources.isEmpty()) View.GONE else View.VISIBLE
        adapter.update(resources)
    }

    private fun showDialog(tag: String, title: String, items: List<ItemSelectorBottomSheet.Item>) {
        showDialog(tag) { ItemSelectorBottomSheet.newInstance(title, items) }
    }
}