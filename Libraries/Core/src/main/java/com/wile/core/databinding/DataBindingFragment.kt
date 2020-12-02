package com.wile.core.databinding

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class DataBindingFragment : Fragment() {

    protected inline fun <reified T : ViewDataBinding> binding(
        @LayoutRes resId: Int
    ): Lazy<T> = lazy { DataBindingUtil.inflate<T>(layoutInflater, resId,
        view?.parent as ViewGroup?, false).apply {
            setLifecycleOwner { this@DataBindingFragment.lifecycle }
        }
    }
}
