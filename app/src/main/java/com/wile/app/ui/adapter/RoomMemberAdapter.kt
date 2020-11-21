package com.wile.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.wile.app.R
import com.wile.app.databinding.ItemTrainingBinding
import com.wile.app.databinding.RoomMemberBinding
import com.wile.database.model.Training
import com.wile.database.model.TrainingTypes

class RoomMemberAdapter : RecyclerView.Adapter<RoomMemberViewHolder>() {

    private val memberList = mutableListOf<String>()
    private var members = mutableMapOf<String, Boolean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomMemberViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<RoomMemberBinding>(inflater, R.layout.room_member, parent, false)
        return RoomMemberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomMemberViewHolder, position: Int) {
        val memberName = memberList[position]
        val tintColor = when( members[memberList[position]]){
            true -> ContextCompat.getColor(holder.itemView.context, R.color.connection_green)
            false -> ContextCompat.getColor(holder.itemView.context, R.color.connection_orange)
            null -> ContextCompat.getColor(holder.itemView.context, R.color.connection_red)
        }

        holder.binding.apply {
            name = memberName
            tint = tintColor
            executePendingBindings()
        }
    }

    override fun getItemCount() = memberList.size

    fun addMemberList(members: HashMap<String, Boolean>) {
        memberList.clear()
        memberList.addAll(members.keys)
        this.members = members
        notifyDataSetChanged()
    }
}
