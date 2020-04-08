package lifetracker.feature.spreadsheets

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import kotlinx.coroutines.launch
import lifetracker.common.auth.AuthData
import lifetracker.feature.spreadsheets.databinding.FragmentSpreadsheetsBinding


class SpreadsheetsFragment(
    private val appName: String,
    private val authData: AuthData,
    private val httpTransport: HttpTransport,
    private val jsonFactory: JsonFactory,
    private val recoverFromError: suspend (Intent) -> Unit,
    private val onSpreadsheetSelected: (String) -> Unit
) : Fragment(R.layout.fragment_spreadsheets) {

    private val driveService: DriveService by lazy {
        DriveService(
            context = requireContext(),
            appName = appName,
            authData = authData,
            httpTransport = httpTransport,
            jsonFactory = jsonFactory,
            recoverFromError = recoverFromError
        )
    }

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
