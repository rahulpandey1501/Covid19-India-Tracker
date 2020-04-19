package com.rpandey.covid19tracker_india.ui

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rpandey.covid19tracker_india.util.Util

abstract class BaseBottomSheetFragment : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), theme)
        bottomSheetDialog.setOnShowListener {
            val bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            val behaviour = bottomSheet?.let { BottomSheetBehavior.from(bottomSheet) }
            behaviour?.let {
                setUpDialogBehaviour(bottomSheetDialog, it)
            }
        }

        return bottomSheetDialog
    }

    open fun setUpDialogBehaviour(
        dialog: BottomSheetDialog,
        behaviour: BottomSheetBehavior<FrameLayout>) {

        val screenHeight = Util.getScreenHeight(requireContext())
        setMaxHeight(dialog, (screenHeight * 3).div(4)) // 75% of the screen
    }

    fun setMaxHeight(bottomSheetDialog: BottomSheetDialog, height: Int) {
        val coordinator = bottomSheetDialog.findViewById<CoordinatorLayout>(com.google.android.material.R.id.coordinator)
        val param = coordinator?.layoutParams as FrameLayout.LayoutParams?
        param?.height = height
        param?.gravity = Gravity.BOTTOM
        coordinator?.layoutParams = param
    }

}