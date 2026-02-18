package com.archo.go.ui.map

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.archo.go.R
import com.archo.go.databinding.FragmentDetailBinding

class DetailFragment : Fragment(R.layout.fragment_detail) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentDetailBinding.bind(view)

        val name = requireArguments().getString(ARG_NAME).orEmpty()
        val lat = requireArguments().getDouble(ARG_LAT)
        val lng = requireArguments().getDouble(ARG_LNG)

        binding.title.text = name
        binding.coords.text = getString(R.string.detail_coords, lat, lng)
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    companion object {
        private const val ARG_NAME = "arg_name"
        private const val ARG_LAT = "arg_lat"
        private const val ARG_LNG = "arg_lng"

        fun newInstance(site: ArchaeologicalSite): DetailFragment {
            return DetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_NAME, site.name)
                    putDouble(ARG_LAT, site.lat)
                    putDouble(ARG_LNG, site.lng)
                }
            }
        }
    }
}
