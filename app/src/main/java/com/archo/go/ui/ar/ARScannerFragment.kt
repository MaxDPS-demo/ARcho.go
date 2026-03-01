package com.archo.go.ui.ar

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.archo.go.R
import com.archo.go.data.InMemoryGameRepository
import com.archo.go.data.ServiceLocator
import com.archo.go.databinding.FragmentArScannerBinding
import com.archo.go.domain.model.Artefact
import com.archo.go.ui.shared.GameViewModel
import com.archo.go.ui.shared.GameViewModelFactory
import com.google.ar.core.ArCoreApk

class ARScannerFragment : Fragment(R.layout.fragment_ar_scanner) {

    private val gameViewModel: GameViewModel by activityViewModels {
        GameViewModelFactory(ServiceLocator.repository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentArScannerBinding.bind(view)

        binding.hintText.text = when (ArCoreApk.getInstance().checkAvailability(requireContext())) {
            ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD,
            ArCoreApk.Availability.SUPPORTED_INSTALLED,
            ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED -> getString(R.string.ar_available_hint)
            else -> getString(R.string.ar_unavailable_hint)
        }

        binding.collectArtefactButton.setOnClickListener {
            val artefact: Artefact = InMemoryGameRepository.defaultArtefact()
            gameViewModel.addArtefact(artefact)
            Toast.makeText(requireContext(), getString(R.string.artefact_collected), Toast.LENGTH_SHORT).show()
        }

        binding.toInventoryButton.setOnClickListener {
            findNavController().navigate(R.id.action_arScannerFragment_to_inventoryFragment)
        }
    }
}
