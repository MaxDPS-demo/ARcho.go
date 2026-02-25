package com.archo.go.ui.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.archo.go.R
import com.archo.go.data.ServiceLocator
import com.archo.go.databinding.FragmentDetailBinding

class DetailFragment : Fragment(R.layout.fragment_detail) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentDetailBinding.bind(view)

        val siteId = arguments?.getString("siteId").orEmpty()
        val site = ServiceLocator.repository.getSiteById(siteId)

        binding.title.text = site?.name ?: getString(R.string.unknown_site)
        binding.description.text = site?.description ?: getString(R.string.no_description)
        binding.coords.text = if (site != null) getString(R.string.detail_coords, site.lat, site.lng) else ""

        binding.startArButton.setOnClickListener {
            findNavController().navigate(R.id.action_detailFragment_to_arScannerFragment)
        }

        binding.toInventoryButton.setOnClickListener {
            findNavController().navigate(R.id.action_detailFragment_to_inventoryFragment)
        }
    }
}
