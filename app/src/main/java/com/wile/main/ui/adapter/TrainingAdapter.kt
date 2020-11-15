package com.wile.main.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.wile.main.R
import com.wile.main.databinding.ItemTrainingBinding
import com.wile.main.model.Training
import com.wile.main.model.TrainingTypes

class TrainingAdapter(
    val onDeleteTraining: (training: Training) -> Unit,
    val onMoveTraining: (trainings: List<Training>) -> Unit,
    val onTouchTraining: (training: Training) -> Unit
) : RecyclerView.Adapter<TrainingViewHolder>() {

    private val items = mutableListOf<Training>()

    val touchListener = TrainingTouchHelperCallback(
        onItemDeleted = ::onItemDeleted,
        onItemMoved = ::onItemMoved,
        onItemDropped = ::onItemDropped
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<ItemTrainingBinding>(inflater, R.layout.item_training, parent, false)
        return TrainingViewHolder(binding).apply {
            binding.root.setOnClickListener {
                val position = adapterPosition.takeIf { it != NO_POSITION }
                    ?: return@setOnClickListener
                onTouchTraining(items[position])
            }
        }
    }

    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        val colorByType = when(items[position].trainingType){
            TrainingTypes.Timed -> ContextCompat.getColor(holder.itemView.context, R.color.timed_preset)
            TrainingTypes.Repeated -> ContextCompat.getColor(holder.itemView.context, R.color.repeated_preset)
            TrainingTypes.Tabata -> ContextCompat.getColor(holder.itemView.context, R.color.tabata_preset)
            TrainingTypes.Custom -> ContextCompat.getColor(holder.itemView.context, R.color.dark_grey)
        }

        val trainingDescription = when(items[position].trainingType) {
            TrainingTypes.Timed -> {
                holder.itemView.context.getString(R.string.training_timed_description)
            }
            TrainingTypes.Repeated -> {
                holder.itemView.context.getString(
                    R.string.training_repeated_description,
                    items[position].reps
                )
            }
            TrainingTypes.Tabata -> {
                items[position].tabataConfig?.let {
                    holder.itemView.context.getString(
                        R.string.training_tabata_description,
                        it.mainDuration,
                        it.alterDuration,
                        it.cycles,
                        it.mainName,
                        it.alterName
                    )
                }
            }
            TrainingTypes.Custom -> {
                if (items[position].reps != 0){
                    holder.itemView.context.getString(R.string.training_repeated_description,
                    items[position].reps)
                } else {
                    holder.itemView.context.getString(R.string.training_timed_description)
                }
            }
        }

        holder.binding.apply {
            training = items[position]
            color = colorByType
            description = trainingDescription
            executePendingBindings()
        }
    }

    override fun getItemCount() = items.size

    fun addTrainingList(trainingList: List<Training>) {
        items.clear()
        items.addAll(trainingList)
        notifyDataSetChanged()
    }

    private fun onItemDeleted(position: Int) {
        onDeleteTraining(items.removeAt(position))
        notifyItemRemoved(position)
    }

    private fun onItemMoved(from: Int, to: Int) {
        items.add(to, items.removeAt(from))
        notifyItemMoved(from, to)
    }

    private fun onItemDropped() {
        onMoveTraining(items)
    }
}
