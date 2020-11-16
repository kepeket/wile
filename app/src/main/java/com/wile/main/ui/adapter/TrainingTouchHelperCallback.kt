package com.wile.main.ui.adapter

import android.content.res.ColorStateList
import android.content.res.Resources
import android.util.Log
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.wile.main.R
import com.wile.main.logging.Logger

class TrainingTouchHelperCallback(
    private val onItemDeleted: (position: Int) -> Unit,
    private val onItemMoved: (from: Int, to: Int) -> Unit,
    private val onItemDropped: () -> Unit
): ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
) {

    lateinit var previousBackgroundColor: ColorStateList
    var previousElevation = 0F
    private val selectedElevation = (3F / Resources.getSystem().displayMetrics.density)

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder is TrainingViewHolder) {
                with(viewHolder) {
                    previousBackgroundColor = binding.cardView2.cardBackgroundColor
                    previousElevation = binding.root.elevation

                    binding.cardView2.setCardBackgroundColor(getColor(itemView.context, R.color.drag))
                    binding.root.elevation = selectedElevation
                }
            }
            Log.i(Logger.TAG, "View has been taken")
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)

        if (viewHolder is TrainingViewHolder) {
            with(viewHolder.binding) {
                cardView2.setCardBackgroundColor(previousBackgroundColor)
                root.elevation = previousElevation
            }
        }

        onItemDropped()
        Log.i(Logger.TAG, "View has been dropped")
    }

    override fun isLongPressDragEnabled() = true

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        Log.i(Logger.TAG, String.format("Moving from %d to %d", viewHolder.adapterPosition, target.adapterPosition))
        onItemMoved(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onItemDeleted(viewHolder.adapterPosition)
    }
}
