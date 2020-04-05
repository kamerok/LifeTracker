package com.kamer.lifetracker.spreadsheets

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import com.kamer.lifetracker.DriveService
import com.kamer.lifetracker.R
import com.kamer.lifetracker.databinding.FragmentSpreadsheetsBinding
import kotlinx.coroutines.launch


class SpreadsheetsFragment(
    private val driveService: DriveService,
    private val onSpreadsheetSelected: (String) -> Unit
) : Fragment(R.layout.fragment_spreadsheets) {

    private val adapter by lazy { SpreadsheetAdapter { onSpreadsheetSelected(it) } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(FragmentSpreadsheetsBinding.bind(view)) {
            recyclerView.adapter = adapter

            viewLifecycleOwner.lifecycle.coroutineScope.launch {
                adapter.setData(driveService.getSpreadsheets())
            }
        }
    }

}
