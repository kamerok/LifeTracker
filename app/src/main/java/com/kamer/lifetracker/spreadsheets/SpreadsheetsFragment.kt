package com.kamer.lifetracker.spreadsheets

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.kamer.lifetracker.DataProvider
import com.kamer.lifetracker.R
import com.kamer.lifetracker.databinding.FragmentSpreadsheetsBinding
import kotlinx.coroutines.launch


class SpreadsheetsFragment : Fragment(R.layout.fragment_spreadsheets) {

    private val adapter by lazy {
        SpreadsheetAdapter {
            DataProvider.prefs.sheetId = it
            findNavController().popBackStack()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(FragmentSpreadsheetsBinding.bind(view)) {
            recyclerView.adapter = adapter

            viewLifecycleOwner.lifecycle.coroutineScope.launch {
                adapter.setData(DataProvider.getSheets())
            }
        }
    }

}
