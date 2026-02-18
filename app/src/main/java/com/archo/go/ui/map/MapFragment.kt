package com.archo.go.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.archo.go.R
import com.archo.go.data.ServiceLocator
import com.archo.go.domain.model.ArchaeologicalSite
import com.archo.go.notifications.NearbyNotificationHelper
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val sites: List<ArchaeologicalSite> by lazy { ServiceLocator.repository.getSites() }

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) enableMyLocationAndCenter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapInnerFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        setupMap()
    }

    private fun setupMap() {
        if (hasFineLocationPermission()) {
            enableMyLocationAndCenter()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        sites.forEach { site ->
            map.addMarker(
                MarkerOptions()
                    .position(LatLng(site.lat, site.lng))
                    .title(site.name)
                    .snippet(site.description)
            )?.tag = site.id
        }

        map.setOnMarkerClickListener { marker ->
            val siteId = marker.tag as? String ?: return@setOnMarkerClickListener false
            findNavController().navigate(
                R.id.action_mapFragment_to_detailFragment,
                Bundle().apply { putString("siteId", siteId) }
            )
            true
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocationAndCenter() {
        if (!hasFineLocationPermission()) return

        map.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true

        val fusedClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedClient.lastLocation.addOnSuccessListener { location ->
            val target = if (location != null) {
                LatLng(location.latitude, location.longitude)
            } else {
                LatLng(48.5764, 19.1256)
            }
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(target, 11f))

            maybeNotifyNearby(location)
        }
    }

    private fun maybeNotifyNearby(playerLocation: android.location.Location?) {
        val player = playerLocation ?: return
        val near = sites.any { site ->
            val result = FloatArray(1)
            Location.distanceBetween(player.latitude, player.longitude, site.lat, site.lng, result)
            result[0] <= 800f
        }
        if (near) NearbyNotificationHelper.notifyNearby(requireContext())
    }

    private fun hasFineLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
}
