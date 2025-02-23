package com.example.sakila.ui.staff

import android.view.LayoutInflater
import android.view.MotionEvent
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
    private val onDeleteClick: (Staff) -> Unit,
    private val onStaffMoved: (List<Staff>) -> Unit // Add this callback
) : ListAdapter<Staff, StaffAdapter.StaffViewHolder>(StaffDiffCallback()),
    ItemTouchHelperAdapter {

    private var itemTouchHelper: ItemTouchHelper? = null // Store the ItemTouchHelper

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

            // Set up drag handle touch listener
            dragHandle.setOnTouchListener { v, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    itemTouchHelper?.startDrag(this) // Start drag on ACTION_DOWN
                }
                false // Return false to allow other touch events
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


    fun setItemTouchHelper(itemTouchHelper: ItemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val newList = currentList.toMutableList()
        Collections.swap(newList, fromPosition, toPosition)
        submitList(newList) // Update the adapter's displayed list
        onStaffMoved(newList) // Notify the ViewModel!
    }

    override fun onItemDismiss(position: Int) {
        //  swipe to delete
    }
}

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)
    fun onItemDismiss(position: Int)
}