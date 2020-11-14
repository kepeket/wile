package com.wile.main.binding

import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.wile.main.model.Preset
import com.wile.main.model.Training
import com.wile.main.model.TrainingTypes
import com.wile.main.ui.adapter.TrainingAdapter
import com.wile.main.ui.adapter.TrainingPresetAdapter

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
            if (it.isNotEmpty()) {
                (view.adapter as? TrainingAdapter)?.addTrainingList(it)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("adapterPresetList")
    fun bindAdapterTrainingPresetList(view: RecyclerView, presetList: List<Preset>?) {
        presetList?.let {
            if (it.isNotEmpty()) {
                (view.adapter as? TrainingPresetAdapter)?.addTrainingPresetList(it)
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