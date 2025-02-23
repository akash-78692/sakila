package com.example.sakila.ui.staff

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sakila.R
import com.example.sakila.data.Staff
import java.util.Collections

class StaffAdapter(
    private val onEditClick: (Staff) -> Unit,
    private val onDeleteClick: (Staff) -> Unit
) : ListAdapter<Staff, StaffAdapter.StaffViewHolder>(StaffDiffCallback()),
    ItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_staff, parent, false)
        return StaffViewHolder(view)
    }

    override fun onBindViewHolder(holder: StaffViewHolder, position: Int) {
        val staff = getItem(position)
        holder.bind(staff)
    }

    inner class StaffViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val staffNameTextView: TextView = itemView.findViewById(R.id.staffNameTextView)
        private val editButton: ImageButton = itemView.findViewById(R.id.editButton)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
        private val dragHandle: View = itemView.findViewById(R.id.dragHandle)

        fun bind(staff: Staff) {
            staffNameTextView.text = staff.name

            editButton.setOnClickListener { onEditClick(staff) }
            deleteButton.setOnClickListener { onDeleteClick(staff) }
            dragHandle.setOnTouchListener { _, _ ->
                itemTouchHelper?.startDrag(this)
                false
            }
        }
    }

    class StaffDiffCallback : DiffUtil.ItemCallback<Staff>() {
        override fun areItemsTheSame(oldItem: Staff, newItem: Staff): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Staff, newItem: Staff): Boolean {
            return oldItem == newItem
        }
    }

    private var itemTouchHelper: ItemTouchHelper? = null

    fun setItemTouchHelper(itemTouchHelper: ItemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val newList = currentList.toMutableList()
        Collections.swap(newList, fromPosition, toPosition)
        submitList(newList) // Important: Use submitList to update the adapter
    }

    override fun onItemDismiss(position: Int) {
        // Handle item dismissal (swipe to delete, if you want to implement it)
    }
}

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)
    fun onItemDismiss(position: Int)
}