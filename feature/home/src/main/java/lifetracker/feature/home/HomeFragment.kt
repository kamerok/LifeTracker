package lifetracker.feature.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.coroutineScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.coroutines.launch
import lifetracker.common.database.Data
import lifetracker.common.domain.SpreadsheetService
import lifetracker.common.domain.Synchronizer
import lifetracker.feature.feed.FeedFragment
import lifetracker.feature.home.databinding.FragmentHomeBinding
import lifetracker.feature.properties.PropertiesFragment
import lifetracker.feature.property.PropertyFragment
import lifetracker.feature.record.RecordFragment
import lifetracker.feature.records.RecordsFragment


class HomeFragment(
    private val data: Data,
    private val spreadsheetService: SpreadsheetService,
    private val synchronizer: Synchronizer
) : Fragment(R.layout.fragment_home) {

    private val navController: NavController
        get() = requireView().findViewById<View>(R.id.fragment_container).findNavController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (!navController.popBackStack()) {
                isEnabled = false
                requireActivity().onBackPressed()
                isEnabled = true
            }
        }
        childFragmentManager.fragmentFactory = object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment =
                when (className) {
                    FeedFragment::class.qualifiedName -> FeedFragment(data) {
                        navController.navigate(R.id.record, RecordFragment.createArgs(it))
                    }
                    PropertiesFragment::class.qualifiedName -> PropertiesFragment(data) {
                        navController.navigate(R.id.property, PropertyFragment.createArgs(it))
                    }
                    PropertyFragment::class.qualifiedName -> PropertyFragment(data) {
                        navController.navigate(R.id.record, RecordFragment.createArgs(it))
                    }
                    RecordFragment::class.qualifiedName -> RecordFragment(data, spreadsheetService)
                    RecordsFragment::class.qualifiedName -> RecordsFragment(data) {
                        navController.navigate(R.id.record, RecordFragment.createArgs(it))
                    }
                    else -> super.instantiate(classLoader, className)
                }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycle.coroutineScope.launch {
            try {
                synchronizer.sync()
            } catch (e: Exception) {
                Log.e("TAG", "omg: ", e)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        with(FragmentHomeBinding.bind(requireView())) {
            bottomNavigationView.setupWithNavController(fragmentContainer.findNavController())
        }
    }

}
