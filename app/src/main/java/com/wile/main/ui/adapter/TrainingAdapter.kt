package com.wile.main.ui.adapter

import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Dimension
import androidx.core.content.ContextCompat.getColor
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.wile.main.R
import com.wile.main.databinding.ItemTrainingBinding
import com.wile.main.model.Training

class TrainingAdapter : RecyclerView.Adapter<TrainingAdapter.TrainingViewHolder>() {

    val items = mutableListOf<Training>()
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

    fun trainingMoved(from: Int, to: Int) {
        val training = items[from]
        items.remove(training)
        items.add(to, training)
        notifyItemMoved(from, to)
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

    val touchListener = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

        lateinit var previousBrackgroundColor: ColorStateList
        var previousElevation = 0F

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, targetViewHolder: RecyclerView.ViewHolder): Boolean {
            Log.i("DD", String.format("Moving from %d to %d", viewHolder.adapterPosition, targetViewHolder.adapterPosition))
            val from = viewHolder.adapterPosition
            val to = targetViewHolder.adapterPosition
            trainingMoved(from, to)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
            //3
            val position = viewHolder.adapterPosition

            listerner?.onDeleteTraining(items[position])
            items.removeAt(position)
            notifyItemRemoved(position)
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                if (viewHolder is TrainingViewHolder) {
                    previousBrackgroundColor = viewHolder.binding.cardView2.cardBackgroundColor
                    viewHolder.binding.cardView2.setCardBackgroundColor(getColor(viewHolder.itemView.context, R.color.drag))
                    previousElevation = viewHolder.binding.root.elevation
                    viewHolder.binding.root.elevation = (3F / Resources.getSystem().displayMetrics.density)
                }
                Log.i("DD", "View has been taken")
            }
            super.onSelectedChanged(viewHolder, actionState)
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            if (viewHolder is TrainingViewHolder) {
                viewHolder.binding.cardView2.setCardBackgroundColor(previousBrackgroundColor)
                viewHolder.binding.root.elevation = previousElevation
            }
            listerner?.onMoveTraining()
            Log.i("DD", "View has been dropped")
        }

        override fun isLongPressDragEnabled() = true
    }

    interface TouchListenerCallbackInterface {
        fun onDeleteTraining(training: Training)
        fun onMoveTraining()
        fun onTouchTraining(training: Training)
    }
}
