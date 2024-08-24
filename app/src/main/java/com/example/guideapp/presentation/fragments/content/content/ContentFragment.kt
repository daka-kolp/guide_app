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
    private val viewModel by viewModels<ContentViewModel>()

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

        val getRouteButton = view.findViewById<Button>(R.id.get_route_button)
        getRouteButton.setOnClickListener {
            //TODO: getSights and currentLocation
            viewModel.getDirections(
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
            viewModel.uiDirectionsState.observe(viewLifecycleOwner) { onViewUpdate(it, view, map) }
        }
    }

    private fun onViewUpdate(
        uiState: ContentViewModel.UIDirectionsState,
        view: View,
        map: GoogleMap
    ) {
        when (uiState) {
            is ContentViewModel.UIDirectionsState.Result -> onRouteFetched(uiState.routes, map)
            is ContentViewModel.UIDirectionsState.Error -> onRouteFetchedError(uiState.error, view.context)
            is ContentViewModel.UIDirectionsState.Empty -> Unit
            is ContentViewModel.UIDirectionsState.Processing -> Unit
        }
    }

    private fun onRouteFetched(routes: List<Route>, map: GoogleMap) {
        val points = routes.first().points
        println("----------------------")
        println(routes.first().getFormattedTime())
        println(routes.first().getFormattedDistance())
        println("----------------------")

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
