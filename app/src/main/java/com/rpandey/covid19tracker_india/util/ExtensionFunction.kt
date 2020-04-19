package com.rpandey.covid19tracker_india.util

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer


fun <T : DialogFragment> Fragment.getDialog(dialogTag: String): T? {
    return childFragmentManager.findFragmentByTag(dialogTag) as T?
}

inline fun <D : DialogFragment> Fragment.showDialog(tag: String, dialogBlock: () -> D): D {
    val dialog = getDialog(tag) ?: dialogBlock()
    if (!dialog.isAdded) {
        dialog.show(childFragmentManager, tag)
    }
    return dialog
}

fun <T : Fragment> Fragment.getChildFragment(fragmentTag: String): T? {
    return childFragmentManager.findFragmentByTag(fragmentTag) as T?
}

inline fun <F : Fragment> Fragment.attachChildFragment(fragmentTag: String, @IdRes container: Int, addToBackStack: Boolean, fragmentBlock: () -> F): F {
    val fragment = getChildFragment(fragmentTag) ?: fragmentBlock()
    if (!fragment.isAdded) {
        val transaction = childFragmentManager.beginTransaction().replace(container, fragment, fragmentTag)
        if (addToBackStack) {
            transaction.addToBackStack(fragmentTag)
        }
        transaction.commitAllowingStateLoss()
    }
    return fragment
}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

fun <T> LiveData<T>.observe(lifecycleOwner: LifecycleOwner, observer: Observer<T>, observeOnce: Boolean = false) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            if (observeOnce) {
                removeObserver(this)
            }
        }
    })
}

inline fun <T> LiveData<T>.observe(fragment: Fragment, crossinline callback: (T) -> Unit) {
    observe(fragment.viewLifecycleOwner, Observer {
        callback(it)
    })
}

inline fun <T> LiveData<T>.observe(activity: AppCompatActivity, crossinline callback: (T) -> Unit) {
    observe(activity, Observer {
        callback(it)
    })
}
