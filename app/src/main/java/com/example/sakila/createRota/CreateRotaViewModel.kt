package com.example.sakila.ui.createRota

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sakila.data.Staff
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CreateRotaViewModel(application: Application) : AndroidViewModel(application) {

    // LiveData for the selected "from" date.  Initialized to today.
    private val _selectedFromDate = MutableLiveData<Date>(Calendar.getInstance().time)
    val selectedFromDate: LiveData<Date> = _selectedFromDate

    // LiveData for the selected "to" date. Initialized to today.
    private val _selectedToDate = MutableLiveData<Date>(Calendar.getInstance().time)
    val selectedToDate: LiveData<Date> = _selectedToDate


    private val _staffList = MutableLiveData<List<Staff>>()
    val staffList: LiveData<List<Staff>> = _staffList

    // LiveData for the Rota data itself.
    //   Key 1: Staff ID (Int)
    //   Key 2: Date
    //   Value:  Hours worked (String - to allow for things like "OFF", "8", "4.5")
    private val _rotaData = MutableLiveData<Map<Int, Map<Date, String>>>()
    val rotaData: LiveData<Map<Int, Map<Date, String>>> = _rotaData

    init {
        // Initialize with the current date as a starting point.
        val today = Calendar.getInstance().time
        _selectedFromDate.value = today
        _selectedToDate.value = today
        // Dummy data.  Replace with actual loading.
        _staffList.value = mutableListOf(
            Staff(1, "Alice"),
            Staff(2, "Bob"),
            Staff(3, "Charlie")
        )
    }
    // Sets the "from" date.
    fun setFromDate(date: Date) {
        _selectedFromDate.value = date
    }

    // Sets the "to" date.
    fun setToDate(date: Date) {
        _selectedToDate.value = date
    }

    // Formats a Date object into the desired string format (e.g., "1st Jun 2025 (Sun)").
    fun getFormattedDate(date: Date): String {
        val dayOfMonth = Calendar.getInstance().apply { time = date }.get(Calendar.DAY_OF_MONTH)
        val daySuffix = when (dayOfMonth) {
            1, 21, 31 -> "st"
            2, 22 -> "nd"
            3, 23 -> "rd"
            else -> "th"
        }
        val format = SimpleDateFormat("d'$daySuffix' MMM yyyy (EEE)", Locale.getDefault())
        return format.format(date)
    }

    // Placeholder for loading staff list
    fun loadStaffList(staffList: List<Staff>){
        _staffList.postValue(staffList)

    }
    // TODO: Add methods for:
    //  -  Loading rota data (if you have saved rota data)
    //  -  Saving rota data
    //  -  Updating the rotaData LiveData when hours are entered in the UI
    //  -  Calculating total hours
}