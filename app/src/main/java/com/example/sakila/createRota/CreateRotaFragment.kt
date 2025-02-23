package com.example.sakila.ui.createRota

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sakila.R  // CORRECT IMPORT! Import your app's R class.
import com.example.sakila.data.Staff
import com.example.sakila.databinding.FragmentCreateRotaBinding
import com.example.sakila.ui.staff.StaffViewModel
import java.util.Calendar
import java.util.Date

class CreateRotaFragment : Fragment() {

    private lateinit var binding: FragmentCreateRotaBinding
    private lateinit var viewModel: CreateRotaViewModel
    private lateinit var staffViewModel: StaffViewModel //for staff list

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateRotaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(CreateRotaViewModel::class.java)
        staffViewModel = ViewModelProvider(requireActivity()).get(StaffViewModel::class.java)//Shared ViewModel

        // Observe LiveData and update UI
        viewModel.selectedFromDate.observe(viewLifecycleOwner) { date ->
            binding.fromDateTextView.text = "From: ${viewModel.getFormattedDate(date)}"
        }

        viewModel.selectedToDate.observe(viewLifecycleOwner) { date ->
            binding.toDateTextView.text = "To: ${viewModel.getFormattedDate(date)}"
        }

        // Set up click listeners for date selection
        binding.fromDateTextView.setOnClickListener { showDatePickerDialog(true) }
        binding.toDateTextView.setOnClickListener { showDatePickerDialog(false) }
        // Observe the staff list and update the table.
        staffViewModel.staffList.observe(viewLifecycleOwner) { staffList ->
            viewModel.loadStaffList(staffList) // Load in CreateRotaViewModel
            updateRotaTable(staffList) // Update UI

        }
        binding.saveButton.setOnClickListener {
            saveRota()
        }

        binding.printButton.setOnClickListener{
            printRota()
        }
    }

    private fun showDatePickerDialog(isFromDate: Boolean) {
        val calendar = Calendar.getInstance()
        val initialDate = if (isFromDate) {
            viewModel.selectedFromDate.value ?: calendar.time
        } else {
            viewModel.selectedToDate.value ?: calendar.time
        }
        calendar.time = initialDate

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }.time

                if (isFromDate) {
                    viewModel.setFromDate(selectedDate)
                } else {
                    viewModel.setToDate(selectedDate)
                }
                staffViewModel.staffList.value?.let { updateRotaTable(it) } //refresh the table
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }
    private fun generateDateHeaders(fromDate: Date, toDate: Date): List<String> {
        val dateHeaders = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        calendar.time = fromDate

        while (calendar.time <= toDate) {
            dateHeaders.add(viewModel.getFormattedDate(calendar.time))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return dateHeaders
    }

    private fun updateRotaTable(staffList: List<Staff>) {
        binding.rotaTableLayout.removeAllViews() // Clear existing rows

        // Get the selected dates (or default to today if null)
        val fromDate = viewModel.selectedFromDate.value ?: Calendar.getInstance().time
        val toDate = viewModel.selectedToDate.value ?: Calendar.getInstance().time

        // 1. Add Header Row
        val headerRow = TableRow(requireContext())
        val headerParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.WRAP_CONTENT,
            TableLayout.LayoutParams.WRAP_CONTENT
        )
        headerRow.layoutParams = headerParams

        val staffNameHeader = TextView(requireContext()).apply {
            text = "Staff Name"
            setPadding(8, 8, 8, 8)
            setTextAppearance(requireContext(),R.style.TextAppearance_AppCompat_Body2) // Corrected!
        }
        headerRow.addView(staffNameHeader)

        val totalHoursHeader = TextView(requireContext()).apply {
            text = "Total Hours"
            setPadding(8, 8, 8, 8)
            setTextAppearance(requireContext(),R.style.TextAppearance_AppCompat_Body2) // Corrected!
        }
        headerRow.addView(totalHoursHeader)

        val copyHeader = TextView(requireContext()).apply { //Replace with Image if required.
            text = "Copy"
            setPadding(8, 8, 8, 8)
            setTextAppearance(requireContext(),R.style.TextAppearance_AppCompat_Body2) // Corrected!

        }
        headerRow.addView(copyHeader)


        // Add Date Headers
        val dateHeaders = generateDateHeaders(fromDate, toDate)
        for (dateHeader in dateHeaders) {
            val dateTextView = TextView(requireContext()).apply {
                text = dateHeader
                setPadding(8, 8, 8, 8)
                setTextAppearance(requireContext(),R.style.TextAppearance_AppCompat_Body2) // Corrected!
            }
            headerRow.addView(dateTextView)
        }

        binding.rotaTableLayout.addView(headerRow)


        // 2. Add Data Rows
        for (staff in staffList) {
            val dataRow = TableRow(requireContext())
            val rowParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )
            dataRow.layoutParams = rowParams

            val staffNameTextView = TextView(requireContext()).apply {
                text = staff.name
                setPadding(8, 8, 8, 8)
                setTextAppearance(requireContext(), R.style.TextAppearance_AppCompat_Body1) // Corrected!
            }
            dataRow.addView(staffNameTextView)

            val totalHoursTextView = TextView(requireContext()).apply {
                text = "0" // Placeholder
                setPadding(8, 8, 8, 8)
                setTextAppearance(requireContext(), R.style.TextAppearance_AppCompat_Body1) // Corrected!
            }
            dataRow.addView(totalHoursTextView)

            val copyIconTextView = TextView(requireContext()).apply { //Replace with Image if required.
                text = "Copy" // Placeholder
                setPadding(8, 8, 8, 8)
            }
            dataRow.addView(copyIconTextView)

            // Add empty cells for each day (you'll populate these later)
            for (dateHeader in dateHeaders) {
                val cellTextView = TextView(requireContext()).apply {
                    text = "" // Initially empty
                    setPadding(8, 8, 8, 8)
                    setTextAppearance(requireContext(), R.style.TextAppearance_AppCompat_Body1) // Corrected!
                }
                dataRow.addView(cellTextView)
            }

            binding.rotaTableLayout.addView(dataRow)
        }
    }

    private fun saveRota() {
        // TODO: Implement saving the rota data.  This will depend on your chosen
        // data storage mechanism (SharedPreferences, Room database, etc.).
        // You'll need to extract the data from the TableLayout and save it.
        // For now, we'll just add a placeholder.
        // This is just example, not the complete one.
        val rotaData = mutableMapOf<Int, Map<Date, String>>()

        // val staffList = viewModel.staffList.value ?: return // Or handle the case where staffList is null
        val fromDate = viewModel.selectedFromDate.value ?: return
        val toDate = viewModel.selectedToDate.value ?: return

        // Iterate through the TableLayout rows (skip the header row)
        for (i in 1 until binding.rotaTableLayout.childCount) {
            val row = binding.rotaTableLayout.getChildAt(i) as? TableRow ?: continue // Skip if not a TableRow

            val staffId = i// Assuming staff ID corresponds to row index.  This is fragile!
            val staffRota = mutableMapOf<Date, String>()

            // Iterate through the cells in the row (skip the first 3 cells: name, total, copy)
            val calendar = Calendar.getInstance()
            calendar.time = fromDate

            for (j in 3 until row.childCount) {
                val cell = row.getChildAt(j) as? TextView ?: continue
                val hours = cell.text.toString()
                staffRota[calendar.time] = hours // Store the hours
                calendar.add(Calendar.DAY_OF_MONTH, 1) // Move to next day
            }
            rotaData[staffId] = staffRota
        }
        //viewModel.saveRotaData(rotaData); //call view model and implement save method.

    }
    private fun printRota() {
        val printManager = requireContext().getSystemService(Context.PRINT_SERVICE) as PrintManager

        val jobName = "${getString(R.string.app_name)} Document"

        // Create a WebView to render the HTML content
        val webView = WebView(requireContext())
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                // Create a PrintDocumentAdapter using the WebView
                val printAdapter = webView.createPrintDocumentAdapter(jobName)

                // Start the print job
                printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
            }
        }

        // Generate HTML content from the rota table
        val htmlContent = generateRotaHtml()

        // Load the HTML content into the WebView
        webView.loadDataWithBaseURL(null, htmlContent, "text/HTML", "UTF-8", null)
    }

    private fun generateRotaHtml(): String {
        val stringBuilder = StringBuilder()

        // Basic HTML structure
        stringBuilder.append("<html><head><title>Rota</title><style>table { width: 100%; border-collapse: collapse; } th, td { border: 1px solid black; padding: 8px; text-align: left; }</style></head><body>")

        // Get the selected dates
        val fromDate = viewModel.selectedFromDate.value ?: Calendar.getInstance().time
        val toDate = viewModel.selectedToDate.value ?: Calendar.getInstance().time
        val dateHeaders = generateDateHeaders(fromDate, toDate)
        val staffList = viewModel.staffList.value ?: return "No Data."


        // Rota table
        stringBuilder.append("<table>")

        // Header row
        stringBuilder.append("<tr><th>Staff Name</th><th>Total Hours</th>")
        dateHeaders.forEach { stringBuilder.append("<th>$it</th>") }
        stringBuilder.append("</tr>")

        // Data rows
        // ... (Iterate through your staff list and rota data) ...
        // 2. Add Data Rows
        for (staff in staffList) {
            stringBuilder.append("<tr>")
            stringBuilder.append("<td>")
            stringBuilder.append(staff.name)
            stringBuilder.append("</td>")
            stringBuilder.append("<td>")
            stringBuilder.append("0") //Calculate Total hour here
            stringBuilder.append("</td>")

            // Add empty cells for each day (you'll populate these later)
            for (dateHeader in dateHeaders) {
                stringBuilder.append("<td>")
                stringBuilder.append("") // Add value here
                stringBuilder.append("</td>")
            }
            stringBuilder.append("</tr>")
        }


        stringBuilder.append("</table>")
        stringBuilder.append("</body></html>")

        return stringBuilder.toString()
    }

}