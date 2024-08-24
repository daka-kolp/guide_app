package com.example.guideapp.presentation.fragments.content.content

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.guideapp.R
import com.example.guideapp.core.domain.entities.Geolocation
import com.example.guideapp.core.domain.entities.Route
import com.example.guideapp.core.domain.entities.Sight
import com.example.guideapp.presentation.helpers.OnAuthLaunch
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContentFragment : Fragment() {
    private val directionsVM by viewModels<DirectionsViewModel>()
    private val sightsVM by viewModels<SightsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val logoutButton = view.findViewById<Button>(R.id.logout_button)
        logoutButton.setOnClickListener { (requireActivity() as OnAuthLaunch).logout() }

        val getSightsButton = view.findViewById<Button>(R.id.get_sights_button)
        getSightsButton.setOnClickListener {
            //TODO: currentLocation
            sightsVM.getSights(Geolocation(47.8353006, 35.1388571))
        }

        val getRouteButton = view.findViewById<Button>(R.id.get_route_button)
        getRouteButton.setOnClickListener {
            //TODO: getSights and currentLocation
            directionsVM.getDirections(
                Geolocation(47.8353006, 35.1388571),
                Geolocation(48.8353006, 36.1388571)
            )
        }

        val childManager = getChildFragmentManager()
        val supportMapFragment = childManager.findFragmentById(R.id.map) as SupportMapFragment
        supportMapFragment.getMapAsync { map ->
            val location = LatLng(47.8353006, 35.1388571)
            val options = MarkerOptions().position(location).title("My position")
            map.addMarker(options)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 8F))
            directionsVM.uiDirectionsState.observe(viewLifecycleOwner) { onDirectionsViewUpdate(it, view, map) }
            sightsVM.uiSightsState.observe(viewLifecycleOwner) { onSightsViewUpdate(it, view, map) }
        }
    }

    private fun onSightsViewUpdate(uiState: SightsViewModel.UISightsState, view: View, map: GoogleMap) {
        when (uiState) {
            is SightsViewModel.UISightsState.Result -> onSightFetched(uiState.sights, map)
            is SightsViewModel.UISightsState.Error -> onSightFetchedError(uiState.error, view.context)
            is SightsViewModel.UISightsState.Empty -> Unit
            is SightsViewModel.UISightsState.Processing -> Unit
        }
    }

    private fun onSightFetched(sights: List<Sight>, map: GoogleMap) {
        sights.forEach {sight ->
            val location = sight.geolocation
            val coordinates = LatLng(location.latitude, location.longitude)
            val options = MarkerOptions().position(coordinates).title(sight.name)
            map.addMarker(options)
        }
    }

    private fun onSightFetchedError(error: String, context: Context) {
        Toast.makeText(
            context,
            "Error, the app can not fetch sights: $error",
            Toast.LENGTH_LONG
        ).show()
    }


    private fun onDirectionsViewUpdate(uiState: DirectionsViewModel.UIDirectionsState, view: View, map: GoogleMap) {
        when (uiState) {
            is DirectionsViewModel.UIDirectionsState.Result -> onRouteFetched(uiState.routes, map)
            is DirectionsViewModel.UIDirectionsState.Error -> onRouteFetchedError(uiState.error, view.context)
            is DirectionsViewModel.UIDirectionsState.Empty -> Unit
            is DirectionsViewModel.UIDirectionsState.Processing -> Unit
        }
    }

    private fun onRouteFetched(routes: List<Route>, map: GoogleMap) {
        val points = routes.first().points
        val decodedPath = PolyUtil.decode(points)
        map.addPolyline(PolylineOptions().addAll(decodedPath))
    }

    private fun onRouteFetchedError(error: String, context: Context) {
        Toast.makeText(
            context,
            "Error, the app can not fetch the route: $error",
            Toast.LENGTH_LONG
        ).show()
    }
}
