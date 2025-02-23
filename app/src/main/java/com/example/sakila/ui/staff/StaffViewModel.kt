package com.example.sakila.ui.staff

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sakila.data.Staff

class StaffViewModel(application: Application) : AndroidViewModel(application) {

    private val _staffList = MutableLiveData<List<Staff>>()
    val staffList: LiveData<List<Staff>> = _staffList

    private var nextStaffId = 1
    private val sharedPreferences = application.getSharedPreferences("staff_prefs", Context.MODE_PRIVATE)

    init {
        loadStaff()
    }

    private fun loadStaff() {
        val staffIdsString = sharedPreferences.getString("staff_order", null)
        val staffIds = staffIdsString?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList()

        val loadedStaff = mutableListOf<Staff>()
        // Load staff data (replace with actual data loading from a database)
        val dummyStaff = listOf(
            Staff(1, "Alice"),
            Staff(2, "Bob"),
            Staff(3, "Charlie")
        )

        // Apply saved order
        if (staffIds.isNotEmpty()) {
            for (id in staffIds) {
                val staffMember = dummyStaff.find { it.id == id }
                staffMember?.let { loadedStaff.add(it) }
            }
            // Add any new staff not in the saved order
            dummyStaff.forEach { staff ->
                if (!loadedStaff.any { it.id == staff.id }) {
                    loadedStaff.add(staff)
                }
            }
            nextStaffId = (loadedStaff.maxOfOrNull { it.id } ?: 0) + 1

        } else {
            loadedStaff.addAll(dummyStaff)
            nextStaffId = (loadedStaff.maxOfOrNull { it.id } ?: 0) + 1
        }

        _staffList.value = loadedStaff
    }

    fun addStaff(name: String) {
        val newList = _staffList.value.orEmpty().toMutableList()
        newList.add(Staff(nextStaffId++, name))
        _staffList.value = newList
        saveStaffOrder()
    }

    fun updateStaff(updatedStaff: Staff) {
        val newList = _staffList.value.orEmpty().toMutableList()
        val index = newList.indexOfFirst { it.id == updatedStaff.id }
        if (index != -1) {
            newList[index] = updatedStaff
            _staffList.value = newList
        }
    }

    fun deleteStaff(staff: Staff) {
        val newList = _staffList.value.orEmpty().toMutableList()
        newList.removeIf { it.id == staff.id }
        _staffList.value = newList
        saveStaffOrder()
    }

    fun updateStaffOrder(staffList: List<Staff>) {
        _staffList.value = staffList
        saveStaffOrder()
    }

    private fun saveStaffOrder() {
        val staffIds = _staffList.value.orEmpty().map { it.id }
        val staffIdsString = staffIds.joinToString(",")
        sharedPreferences.edit().putString("staff_order", staffIdsString).apply()
    }

    override fun onCleared() {
        super.onCleared()
        saveStaffOrder() // Save when ViewModel is cleared
    }
}