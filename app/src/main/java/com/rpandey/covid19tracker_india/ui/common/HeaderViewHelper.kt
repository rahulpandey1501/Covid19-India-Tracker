package com.rpandey.covid19tracker_india.ui.common

import android.view.View
import android.widget.ImageView

class HeaderViewHelper(
    private val headerArrowViews: List<ViewSortModel>,
    private val defaultSortOn: Pair<SortOn, Boolean>? = SortOn.CONFIRMED to false,
    private val performSort: (SortOn, Boolean) -> Unit) {

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