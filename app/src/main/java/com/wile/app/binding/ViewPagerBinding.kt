package com.wile.app.binding

import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2
import com.wile.app.ui.adapter.WorkoutAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
object ViewPagerBinding {

    @JvmStatic
    @BindingAdapter("adapter")
    fun bindAdapter(view: ViewPager2, adapter: WorkoutAdapter) {
        view.adapter = adapter
    }


    @JvmStatic
    @BindingAdapter("adapterWorkoutList")
    fun bindAdapterTrainingList(view: ViewPager2, workoutList: List<Int>?) {
        workoutList?.let {
            if (it.isNotEmpty()) {
                (view.adapter as? WorkoutAdapter)?.addWorkoutList(it)
            }
        }
    }

}