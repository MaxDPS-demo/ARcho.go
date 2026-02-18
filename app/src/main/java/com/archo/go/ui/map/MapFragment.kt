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
        ArchaeologicalSite(
            name = "Pustý hrad",
            description = "Rozsiahla zrúcanina hradu nad Zvolenom s nálezmi z obdobia stredoveku.",
            lat = 48.5795,
            lng = 19.1462
        ),
        ArchaeologicalSite(
            name = "Stará radnica Zvolen",
            description = "Historická mestská lokalita s archeologickými stopami stredovekého osídlenia.",
            lat = 48.5764,
            lng = 19.1256
        ),
        ArchaeologicalSite(
            name = "Rímskokatolícky kostol Dobrá Niva",
            description = "Sakrálny objekt s historickými vrstvami osídlenia a regionálnymi artefaktmi.",
            lat = 48.3982,
            lng = 19.1103
        ),
        ArchaeologicalSite(
            name = "Petuša",
            description = "Archeologicky významné hradisko pri sútoku Hrona a Slatiny.",
            lat = 48.5751,
            lng = 19.1805
        ),
        ArchaeologicalSite(
            name = "Šášov",
            description = "Hradná lokalita so stopami fortifikácie a nálezmi z neskorého stredoveku.",
            lat = 48.5454,
            lng = 18.8407
        ),
        ArchaeologicalSite(
            name = "Revište",
            description = "Zrúcanina hradu Revište s výhľadom na Hron a bohatou historickou stratigrafiou.",
            lat = 48.4639,
            lng = 18.9182
        )
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
                    .snippet(site.description)
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
                LatLng(48.5764, 19.1256) // fallback Zvolen
            }
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(target, 11f))
        }
    }

    private fun hasFineLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}
