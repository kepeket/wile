package com.wile.main.ui.adapter

import android.os.SystemClock
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.wile.main.R
import com.wile.main.databinding.ItemTrainingBinding
import com.wile.main.model.Training

class TrainingAdapter : RecyclerView.Adapter<TrainingAdapter.TrainingViewHolder>() {

    private val items: MutableList<Training> = mutableListOf()
    private var onClickedAt = 0L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<ItemTrainingBinding>(inflater, R.layout.item_training, parent, false)
        return TrainingViewHolder(binding).apply {
            binding.root.setOnClickListener {
                val position = adapterPosition.takeIf { it != NO_POSITION }
                    ?: return@setOnClickListener
                val currentClickedAt = SystemClock.elapsedRealtime()
                    //DetailActivity.startActivity(binding.transformationLayout, items[position])
                    onClickedAt = currentClickedAt
            }
        }
    }

    fun addTrainingList(trainingList: List<Training>) {
        val previous = items.size
        items.addAll(trainingList)
        notifyItemRangeChanged(previous, trainingList.size)
    }

    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        holder.binding.apply {
            training = items[position]
            executePendingBindings()
        }
    }

    override fun getItemCount() = items.size

    class TrainingViewHolder(val binding: ItemTrainingBinding) :
        RecyclerView.ViewHolder(binding.root)
}