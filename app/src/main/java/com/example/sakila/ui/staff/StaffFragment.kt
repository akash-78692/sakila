package com.example.sakila.ui.staff

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView // Import RecyclerView
import com.example.sakila.data.Staff
import com.example.sakila.databinding.FragmentStaffBinding
import com.google.android.material.textfield.TextInputEditText

class StaffFragment : Fragment() {

    private lateinit var viewModel: StaffViewModel
    private lateinit var adapter: StaffAdapter
    private var _binding: FragmentStaffBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStaffBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(StaffViewModel::class.java)

        // Initialize Adapter with the new callback
        adapter = StaffAdapter(
            onEditClick = { staff -> showEditStaffDialog(staff) },
            onDeleteClick = { staff -> showDeleteConfirmationDialog(staff) },
            onStaffMoved = { newList -> viewModel.updateStaffOrder(newList) } // Pass the callback
        )

        binding.staffRecyclerView.adapter = adapter
        binding.staffRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.staffList.observe(viewLifecycleOwner) { staffList ->
            adapter.submitList(staffList)
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.staffRecyclerView)
        adapter.setItemTouchHelper(itemTouchHelper)

        binding.fabAddStaff.setOnClickListener {
            showAddStaffDialog()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showAddStaffDialog() {
        val inputEditText = TextInputEditText(requireContext())
        inputEditText.hint = "Staff Name"

        AlertDialog.Builder(requireContext())
            .setTitle("Add Staff")
            .setView(inputEditText)
            .setPositiveButton("Add") { _, _ ->
                val staffName = inputEditText.text.toString().trim()
                if (staffName.isNotBlank()) {
                    viewModel.addStaff(staffName)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditStaffDialog(staff: Staff) {
        val inputEditText = TextInputEditText(requireContext())
        inputEditText.setText(staff.name)
        inputEditText.hint = "Staff Name"

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Staff")
            .setView(inputEditText)
            .setPositiveButton("Update") { _, _ ->
                val newName = inputEditText.text.toString().trim()
                if (newName.isNotBlank()) {
                    val updatedStaff = staff.copy(name = newName)
                    viewModel.updateStaff(updatedStaff)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirmationDialog(staff: Staff) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Staff")
            .setMessage("Are you sure you want to delete ${staff.name}?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteStaff(staff)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    // Corrected ItemTouchHelper.SimpleCallback
    private val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition
            adapter.onItemMove(fromPosition, toPosition)
            // viewModel.updateStaffOrder(adapter.currentList) // Moved to Adapter.
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            // No swipe-to-dismiss
        }


    }
}