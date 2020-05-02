package com.rpandey.covid19tracker_india.ui.common

import android.view.View
import android.widget.ImageView
import com.rpandey.covid19tracker_india.databinding.ViewHeaderCombinedBinding

class HeaderViewHelper(
    binding: ViewHeaderCombinedBinding,
    private val defaultSortOn: Pair<SortOn, Boolean>? = SortOn.CONFIRMED to false,
    private val performSort: (SortOn, Boolean) -> Unit) {

    private val headerArrowViews = mutableListOf (
        ViewSortModel(binding.headerConfirmed, binding.confirmSortArrow, SortOn.CONFIRMED),
        ViewSortModel(binding.headerActive, binding.activeSortArrow, SortOn.ACTIVE),
        ViewSortModel(binding.headerRecovered, binding.recoverSortArrow, SortOn.RECOVERED),
        ViewSortModel(binding.headerDeaths, binding.deathSortArrow, SortOn.DECEASED)
    )

    fun init() {
        defaultSortOn?.let { defaultSort ->
            val imageView = headerArrowViews.find { it.sortOn == defaultSort.first }?.arrowImage
            imageView?.rotation = if (defaultSort.second) 180f else 0f
            imageView?.visibility = View.VISIBLE
        }

        headerArrowViews.forEach { header ->
            header.container.setOnClickListener {
                setArrowUi(header.arrowImage, header.sortOn)
            }
        }
    }

    fun addMoreViews(container: View, arrowImage: ImageView, sortOn: SortOn) {
        headerArrowViews.add(ViewSortModel(container, arrowImage, sortOn))
    }

    private fun setArrowUi(currentArrowImage: ImageView, sortOn: SortOn) {
        if (currentArrowImage.visibility == View.GONE) {
            currentArrowImage.visibility = View.VISIBLE
            currentArrowImage.rotation = 0f
            performSort(sortOn, false)

        } else {
            val isCurrentOrderIsAsc = currentArrowImage.rotation == 180f // ascending
            currentArrowImage.rotation = if (isCurrentOrderIsAsc) 0f else 180f
            performSort(sortOn, !isCurrentOrderIsAsc)
        }

        headerArrowViews.forEach {
            if (it.arrowImage != currentArrowImage) {
                it.arrowImage.visibility = View.GONE
            }
        }
    }
}

class ViewSortModel(var container: View, var arrowImage: ImageView, var sortOn: SortOn)