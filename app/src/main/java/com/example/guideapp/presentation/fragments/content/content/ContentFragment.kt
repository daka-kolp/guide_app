package com.example.guideapp.presentation.fragments.content.content

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
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
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContentFragment : Fragment(), GoogleMap.OnMarkerClickListener {
    private val directionsVM by viewModels<DirectionsViewModel>()
    private val sightsVM by viewModels<SightsViewModel>()
    private val locationVM by viewModels<CurrentLocationViewModel>()
    private var polyline: Polyline? = null
    private var previewImage: ImageView? = null
    private var timeAndDistance: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askGeolocationPermissions()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        locationVM.getCurrentLocation()

        val childManager = getChildFragmentManager()
        val supportMapFragment = childManager.findFragmentById(R.id.map) as SupportMapFragment
        supportMapFragment.getMapAsync { mapCallback(it) }

        setupAppBar()

        previewImage = view.findViewById(R.id.sight_preview_image)
        timeAndDistance = view.findViewById(R.id.time_distance)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        directionsVM.getDirections(marker.position.geolocationFromLatLng())
        marker.showInfoWindow()
        setSightImage(marker.snippet)
        return true
    }

    private fun mapCallback(map: GoogleMap) {
        directionsVM.uiDirectionsState.observe(viewLifecycleOwner) { onDirectionsViewUpdate(it, map) }
        sightsVM.uiSightsState.observe(viewLifecycleOwner) { onSightsViewUpdate(it, map) }
        locationVM.uiCurrentLocationState.observe(viewLifecycleOwner) { onCurrentLocationViewUpdate(it, map) }
        map.setOnMarkerClickListener(this)
    }

    private fun onDirectionsViewUpdate(uiState: DirectionsViewModel.UIDirectionsState, map: GoogleMap) {
        when (uiState) {
            is DirectionsViewModel.UIDirectionsState.Result -> onRouteFetched(uiState.routes, map)
            is DirectionsViewModel.UIDirectionsState.Error -> onRouteFetchedError(uiState.error)
            is DirectionsViewModel.UIDirectionsState.Empty -> Unit
            is DirectionsViewModel.UIDirectionsState.Processing -> Unit
        }
    }

    private fun onRouteFetched(routes: List<Route>, map: GoogleMap) {
        polyline?.remove()
        routes.forEach { route ->
            val decodedPath = PolyUtil.decode(route.points)
            val options = PolylineOptions().color(Color.RED)
            polyline = map.addPolyline(options.addAll(decodedPath))
        }
        setTimeAndDistance(routes.first())
    }

    private fun onRouteFetchedError(error: String) {
        Toast.makeText(context, "Error, the app can not fetch the route: $error", Toast.LENGTH_LONG).show()
    }

    private fun onSightsViewUpdate(uiState: SightsViewModel.UISightsState, map: GoogleMap) {
        when (uiState) {
            is SightsViewModel.UISightsState.Result -> onSightFetched(uiState.sights, map)
            is SightsViewModel.UISightsState.Error -> onSightFetchedError(uiState.error)
            is SightsViewModel.UISightsState.Empty -> Unit
            is SightsViewModel.UISightsState.Processing -> Unit
        }
    }

    private fun onSightFetched(sights: List<Sight>, map: GoogleMap) {
        sights.forEach { sight ->
            val coordinates = sight.geolocation.latLngFromGeolocation()
            val options = MarkerOptions().position(coordinates).title(sight.name).snippet(sight.photo)
            map.addMarker(options)
        }
    }

    private fun onSightFetchedError(error: String) {
        Toast.makeText(context, "Error, the app can not fetch sights: $error", Toast.LENGTH_LONG).show()
    }


    private fun onCurrentLocationViewUpdate(uiState: CurrentLocationViewModel.UICurrentLocationState, map: GoogleMap) {
        when (uiState) {
            is CurrentLocationViewModel.UICurrentLocationState.Result -> onCurrentLocationFetched(uiState.location, map)
            is CurrentLocationViewModel.UICurrentLocationState.Error -> onCurrentLocationFetchedError(uiState.error)
            is CurrentLocationViewModel.UICurrentLocationState.Empty -> Unit
            is CurrentLocationViewModel.UICurrentLocationState.Processing -> Unit
        }
    }

    private fun onCurrentLocationFetched(currentLocation: Location, map: GoogleMap) {
        map.clear()
        val location = currentLocation.geolocationFromLocation()
        directionsVM.origin = location
        sightsVM.getSights(location)
        val latLng = currentLocation.latLngFromLocation()
        val options = MarkerOptions().icon(getUserIcon()).position(latLng).title("My current position")
        map.addMarker(options)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.5F))
    }

    private fun onCurrentLocationFetchedError(error: String) {
        Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
    }

    private fun getUserIcon(): BitmapDescriptor {
        val size = 124
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.user_location)
        val marker = Bitmap.createScaledBitmap(bitmap, size, size, false)
        return BitmapDescriptorFactory.fromBitmap(marker)
    }

    private fun askGeolocationPermissions() {
        val permissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            val afl = Manifest.permission.ACCESS_FINE_LOCATION
            val acl = Manifest.permission.ACCESS_COARSE_LOCATION
            if (it.getOrDefault(afl, false) || it.getOrDefault(acl, false)) {
                locationVM.getCurrentLocation()
            }
        }
        permissionRequest.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }

    private fun setupAppBar() {
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(activity.findViewById(R.id.app_bar))
        activity.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.reload_button -> {
                        setSightImage(null)
                        setTimeAndDistance(null)
                        locationVM.getCurrentLocation()
                        true
                    }
                    R.id.show_sights_button -> {
                        showSights()
                        true
                    }
                    R.id.logout_button -> {
                        (requireActivity() as OnAuthLaunch).logout()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setSightImage(imagePath: String?) {
        Picasso.get().load(imagePath).into(previewImage)
    }

    private fun setTimeAndDistance(route: Route?) {
        timeAndDistance?.isVisible = route != null
        val text = if (route != null) route.getFormattedTime() + ", " + route.getFormattedDistance() else ""
        timeAndDistance?.text = text
    }

    private fun showSights() {
        val state = sightsVM.uiSightsState.value
        if (state is SightsViewModel.UISightsState.Result) {
            parentFragmentManager
                .beginTransaction()
                .add(R.id.container, SightsFragment(state.origin, state.sights))
                .addToBackStack("SightsFragment")
                .commit()
        } else if (state is SightsViewModel.UISightsState.Error) {
            onSightFetchedError(state.error)
        }
    }

    private fun LatLng.geolocationFromLatLng(): Geolocation {
        return Geolocation(latitude, longitude)
    }

    private fun Location.geolocationFromLocation(): Geolocation {
        return Geolocation(latitude, longitude)
    }

    private fun Geolocation.latLngFromGeolocation(): LatLng {
        return LatLng(latitude, longitude)
    }

    private fun Location.latLngFromLocation(): LatLng {
        return LatLng(latitude, longitude)
    }
}
