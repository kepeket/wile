package com.wile.main.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.wile.main.model.Training
import com.wile.main.ui.adapter.TrainingAdapter
import com.wile.main.ui.main.MainViewModel

object RecyclerViewBinding {

    @JvmStatic
    @BindingAdapter("adapter")
    fun bindAdapter(view: RecyclerView, adapter: RecyclerView.Adapter<*>) {
        view.adapter = adapter
    }


    @JvmStatic
    @BindingAdapter("adapterTrainingList")
    fun bindAdapterTrainingList(view: RecyclerView, trainingList: List<Training>?) {
        trainingList?.let {
            if (it.count() > 0) {
                (view.adapter as? TrainingAdapter)?.addTrainingList(it)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("itemTouchListener")
    fun bindAdapterTrainingList(view: RecyclerView, listener: ItemTouchHelper.SimpleCallback?) {
        listener?.let {
            val itemTouchHelper = ItemTouchHelper(it)
            itemTouchHelper.attachToRecyclerView(view)
        }
    }
}