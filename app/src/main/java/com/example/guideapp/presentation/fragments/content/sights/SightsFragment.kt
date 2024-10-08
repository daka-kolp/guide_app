package com.example.guideapp.presentation.fragments.content.sights

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.guideapp.R
import com.example.guideapp.core.domain.entities.Geolocation
import com.example.guideapp.core.domain.entities.Sight
import com.example.guideapp.presentation.fragments.content.content.SightsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SightsFragment(private val origin: Geolocation? = null) : Fragment() {
    private val sightsVM by viewModels<SightsViewModel>()
    private val locationVM by viewModels<LocationViewModel>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeContainer: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationVM.saveLocation(origin)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_sights, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getSights()

        swipeContainer = view.findViewById(R.id.refreshLayout)
        swipeContainer.setOnRefreshListener { getSights() }

        recyclerView = view.findViewById(R.id.results_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        sightsVM.uiSightsState.observe(viewLifecycleOwner) { onSightsViewUpdate(it, view) }
    }

    private fun getSights() {
        val location = locationVM.valueToUpdate.value
        if (location != null) {
            sightsVM.getSights(location)
        }
    }

    private fun onSightsViewUpdate(uiState: SightsViewModel.UISightsState, view: View) {
        swipeContainer.isRefreshing = false

        when (uiState) {
            is SightsViewModel.UISightsState.Result -> onSightFetched(uiState.sights)
            is SightsViewModel.UISightsState.Error -> onSightFetchedError(uiState.error, view.context)
            is SightsViewModel.UISightsState.Empty -> Unit
            is SightsViewModel.UISightsState.Processing -> Unit
        }
    }

    private fun onSightFetched(sights: List<Sight>) {
        val adapter = ResultsRecycleViewAdapter(sights as ArrayList<Sight>)
        recyclerView.adapter = adapter
    }

    private fun onSightFetchedError(error: String, context: Context) {
        Toast.makeText(
            context,
            "Error, the app can not fetch sights: $error",
            Toast.LENGTH_LONG
        ).show()
    }
}
