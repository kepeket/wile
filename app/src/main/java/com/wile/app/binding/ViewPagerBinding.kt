package com.wile.app.binding

import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2
import com.wile.app.ui.adapter.WorkoutListingAdapter

object ViewPagerBinding {

    @JvmStatic
    @BindingAdapter("adapter")
    fun bindAdapter(view: ViewPager2, adapter: WorkoutListingAdapter) {
        view.adapter = adapter
    }


    @JvmStatic
    @BindingAdapter("adapterWorkoutList")
    fun bindAdapterTrainingList(view: ViewPager2, workoutList: List<Int>?) {
        workoutList?.let {
            if (it.isNotEmpty()) {
                (view.adapter as? WorkoutListingAdapter)?.addWorkoutList(it)
            }
        }
    }

}