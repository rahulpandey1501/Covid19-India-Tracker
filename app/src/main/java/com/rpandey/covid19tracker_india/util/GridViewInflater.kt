package com.rpandey.covid19tracker_india.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

class GridViewInflater(private val column: Int, private val container: ViewGroup, private val callback: GridViewInflater.() -> Unit) {

    init {
        container.postDelayed({
            container.removeAllViews()
            callback()
        }, 5)
    }

    private val parentLL by lazy {
        val parentLL = LinearLayout(container.context).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            orientation = LinearLayout.VERTICAL
        }
        container.addView(parentLL)
        parentLL
    }

    fun <B: ViewDataBinding> addView(@LayoutRes layoutRes: Int): B {
        val context = container.context
        val rowLL = getRowLL(context)
        val binding = DataBindingUtil.inflate<B>(LayoutInflater.from(context), layoutRes, rowLL, true)
        setItemParam(binding.root)
        return binding
    }

    fun addView(view: View) {
        val context = view.context
        val rowLL = getRowLL(context)
        rowLL.addView(view)
        setItemParam(view)
    }

    private fun setItemParam(view: View) {
        val param = view.layoutParams as LinearLayout.LayoutParams
        val marginStart = param.marginStart
        val marginEnd = param.marginEnd
        var allocatedWidth = container.width / column
        allocatedWidth = allocatedWidth - marginEnd - marginStart
        param.width = allocatedWidth
        param.height = LinearLayout.LayoutParams.MATCH_PARENT
        view.layoutParams = param
    }

    private fun getRowLL(context: Context): LinearLayout {
        val currentRowCount = parentLL.childCount

        if (currentRowCount > 0) {
            val lastRowLL = parentLL.getChildAt(currentRowCount - 1) as LinearLayout // get last rowView
            if (lastRowLL.childCount < column)
                return lastRowLL
        }

        // else return new rowLL view
        val rowView = LinearLayout(context).apply {
            layoutParams = getRowItemParam()
            orientation = LinearLayout.HORIZONTAL
        }
        parentLL.addView(rowView)
        return rowView
    }

    private fun getRowItemParam(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    }
}