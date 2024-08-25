package com.example.guideapp.presentation.fragments.content.content

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.guideapp.R
import com.example.guideapp.core.domain.entities.Geolocation
import com.example.guideapp.core.domain.entities.Route
import com.example.guideapp.core.domain.entities.Sight
import com.example.guideapp.presentation.fragments.content.sights.SightsFragment
import com.example.guideapp.presentation.helpers.OnAuthLaunch
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContentFragment : Fragment(), GoogleMap.OnMarkerClickListener {
    private val directionsVM by viewModels<DirectionsViewModel>()
    private val sightsVM by viewModels<SightsViewModel>()
    private var polyline: Polyline? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sightsVM.getSights()

        val showSightsButton = view.findViewById<Button>(R.id.show_sights_button)
        showSightsButton.setOnClickListener { showSights() }

        val logoutButton = view.findViewById<Button>(R.id.logout_button)
        logoutButton.setOnClickListener { (requireActivity() as OnAuthLaunch).logout() }

        val childManager = getChildFragmentManager()
        val supportMapFragment = childManager.findFragmentById(R.id.map) as SupportMapFragment
        supportMapFragment.getMapAsync { mapCallback(it, view) }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        directionsVM.getDirections(marker.position.toGeolocation())
        marker.showInfoWindow()
        return true
    }

    private fun showSights() {
        val state = sightsVM.uiSightsState.value
        if (state is SightsViewModel.UISightsState.Result)
            parentFragmentManager.beginTransaction()
                .add(R.id.container, SightsFragment(state.sights))
                .addToBackStack("SightsFragment")
                .commit()
    }

    private fun mapCallback(map: GoogleMap, view: View) {
        //TODO: get currentLocation
        val location = LatLng(47.8353006, 35.1388571)
        val options = MarkerOptions().icon(getUserIcon()).position(location).title("My current position")
        map.addMarker(options)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14.5F))
        directionsVM.uiDirectionsState.observe(viewLifecycleOwner) { onDirectionsViewUpdate(it, view, map) }
        sightsVM.uiSightsState.observe(viewLifecycleOwner) { onSightsViewUpdate(it, view, map) }
        map.setOnMarkerClickListener(this)
    }

    private fun getUserIcon(): BitmapDescriptor {
        val size = 124
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.user_location)
        val marker = Bitmap.createScaledBitmap(bitmap, size, size, false)
        return BitmapDescriptorFactory.fromBitmap(marker)
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
        polyline?.remove()
        routes.forEach { route ->
            val decodedPath = PolyUtil.decode(route.points)
            polyline = map.addPolyline(PolylineOptions().addAll(decodedPath))
        }
    }

    private fun onRouteFetchedError(error: String, context: Context) {
        Toast.makeText(
            context,
            "Error, the app can not fetch the route: $error",
            Toast.LENGTH_LONG
        ).show()
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
        sights.forEach { sight ->
            val coordinates = sight.geolocation.toLatLng()
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

    private fun LatLng.toGeolocation(): Geolocation {
        return Geolocation(latitude, longitude)
    }

    private fun Geolocation.toLatLng(): LatLng {
        return LatLng(latitude, longitude)
    }
}
