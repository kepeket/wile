package com.wile.main.ui.adapter

import android.os.SystemClock
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.wile.main.R
import com.wile.main.databinding.ItemTrainingBinding
import com.wile.main.model.Training

class TrainingAdapter : RecyclerView.Adapter<TrainingAdapter.TrainingViewHolder>() {

    private val items = mutableListOf<Training>()
    private var onClickedAt = 0L
    private var listerner: TouchListenerCallbackInterface? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<ItemTrainingBinding>(inflater, R.layout.item_training, parent, false)
        return TrainingViewHolder(binding).apply {
            binding.root.setOnClickListener {
                val position = adapterPosition.takeIf { it != NO_POSITION }
                    ?: return@setOnClickListener
                    listerner?.onTouchTraining(items[position])
            }
        }
    }

    fun addTrainingList(trainingList: List<Training>) {
        items.clear()
        items.addAll(trainingList)
        notifyDataSetChanged()
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

    fun setTouchListener(listener_: TouchListenerCallbackInterface?) {
        listener_?.let{
            listerner = it
        }
    }

    val touchListener = object : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder1: RecyclerView.ViewHolder): Boolean {
            //2
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
            //3
            val position = viewHolder.adapterPosition

            listerner?.let {
                it.onDeleteTraining(items[position])
            }
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    interface TouchListenerCallbackInterface {
        fun onDeleteTraining(training: Training)
        fun onMoveTraining(training: Training)
        fun onTouchTraining(training: Training)
    }
}
