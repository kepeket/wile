package com.wile.app.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.wile.app.ui.main.TrainingListingFragment
import com.wile.database.model.Training
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

@ExperimentalCoroutinesApi
class WorkoutAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    private val items = mutableListOf<Int>()

    fun addWorkoutList(workout: List<Int>){
        items.clear()
        items.addAll(workout)
        notifyDataSetChanged()
    }

    fun getNextId(): Int {
        return Collections.max(items) + 1
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun createFragment(position: Int): Fragment {
        val workoutId = items[position]
        return TrainingListingFragment.newFragment(workoutId)
    }

    fun addWorkout(): Int{
        items.add(getNextId())
        notifyDataSetChanged()
        return itemCount -1
    }

    fun deleteWorkout(workout: Int){
        val removedPos = items.indexOf(workout)
        items.remove(removedPos)
        notifyItemRemoved(removedPos)
    }
}