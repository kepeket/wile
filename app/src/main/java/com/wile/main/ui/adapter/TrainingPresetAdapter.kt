package com.wile.main.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.wile.main.R
import com.wile.main.databinding.ItemTrainingPresetBinding
import com.wile.main.model.Preset
import com.wile.database.model.TrainingTypes

class TrainingPresetAdapter(
    val onTouchPreset: (preset: Preset) -> Unit
) : RecyclerView.Adapter<TrainingPresetViewHolder>() {

    private val items = mutableListOf<Preset>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingPresetViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<ItemTrainingPresetBinding>(inflater, R.layout.item_training_preset, parent, false)
        return TrainingPresetViewHolder(binding).apply {
            binding.root.setOnClickListener {
                val position = adapterPosition.takeIf { it != NO_POSITION }
                    ?: return@setOnClickListener
                onTouchPreset(items[position])
            }
        }
    }

    override fun onBindViewHolder(holder: TrainingPresetViewHolder, position: Int) {
        val colorByType = when(items[position].trainingType){
            TrainingTypes.Timed -> ContextCompat.getColor(holder.itemView.context, R.color.timed_preset)
            TrainingTypes.Repeated -> ContextCompat.getColor(holder.itemView.context, R.color.repeated_preset)
            TrainingTypes.Tabata -> ContextCompat.getColor(holder.itemView.context, R.color.tabata_preset)
            TrainingTypes.Custom -> ContextCompat.getColor(holder.itemView.context, R.color.dark_grey)
        }

        holder.binding.apply {
            preset = items[position]
            color = colorByType
            executePendingBindings()
        }
    }

    override fun getItemCount() = items.size

    fun addTrainingPresetList(presetList: List<Preset>) {
        items.clear()
        items.addAll(presetList)
        notifyDataSetChanged()
    }
}
