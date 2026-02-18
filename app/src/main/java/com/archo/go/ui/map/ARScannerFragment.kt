package com.archo.go.ui.map

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.archo.go.R
import com.archo.go.databinding.FragmentArScannerBinding

class ARScannerFragment : Fragment(R.layout.fragment_ar_scanner) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentArScannerBinding.bind(view)
        binding.statusText.text = getString(R.string.ar_scanner_placeholder)
    }
}
