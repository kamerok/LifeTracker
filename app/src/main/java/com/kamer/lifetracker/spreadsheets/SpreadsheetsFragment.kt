package com.kamer.lifetracker.spreadsheets

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.kamer.lifetracker.DataProvider
import com.kamer.lifetracker.DriveService
import com.kamer.lifetracker.R
import com.kamer.lifetracker.databinding.FragmentSpreadsheetsBinding
import kotlinx.coroutines.launch


class SpreadsheetsFragment(
    private val driveService: DriveService
) : Fragment(R.layout.fragment_spreadsheets) {

    private val adapter by lazy {
        SpreadsheetAdapter {
            DataProvider.prefs.sheetId = it
            findNavController().navigate(R.id.action_spreadsheets_fragment_to_home_fragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(FragmentSpreadsheetsBinding.bind(view)) {
            recyclerView.adapter = adapter

            viewLifecycleOwner.lifecycle.coroutineScope.launch {
                adapter.setData(driveService.getSpreadsheets())
            }
        }
    }

}
