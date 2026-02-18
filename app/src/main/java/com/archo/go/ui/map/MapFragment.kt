package com.archo.go.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.archo.go.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    private val sitePoints = listOf(
        ArchaeologicalSite("Keltské hradisko Devín", 48.1738, 16.9784),
        ArchaeologicalSite("Gerulata Rusovce", 48.0346, 17.1560),
        ArchaeologicalSite("Múzeum mesta Bratislavy", 48.1425, 17.1081),
        ArchaeologicalSite("Slovenské národné múzeum", 48.1416, 17.1009)
    )

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

        sitePoints.forEach { site ->
            map.addMarker(
                MarkerOptions()
                    .position(LatLng(site.lat, site.lng))
                    .title(site.name)
            )?.tag = site
        }

        map.setOnMarkerClickListener { marker ->
            val site = marker.tag as? ArchaeologicalSite ?: return@setOnMarkerClickListener false
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, DetailFragment.newInstance(site))
                .addToBackStack("detail")
                .commit()
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
                LatLng(48.1486, 17.1077) // fallback Bratislava
            }
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(target, 12f))
        }
    }

    private fun hasFineLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}
