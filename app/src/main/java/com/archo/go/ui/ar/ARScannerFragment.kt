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
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.ux.TransformableNode

class ARScannerFragment : Fragment(R.layout.fragment_ar_scanner) {

    private val gameViewModel: GameViewModel by activityViewModels {
        GameViewModelFactory(ServiceLocator.repository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentArScannerBinding.bind(view)

        if (childFragmentManager.findFragmentById(R.id.arFragmentContainer) == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.arFragmentContainer, SimpleArFragment())
                .commitNow()
        }

        val arFragment = childFragmentManager.findFragmentById(R.id.arFragmentContainer) as SimpleArFragment

        binding.hintText.text = getString(R.string.ar_instruction)

        var placed = false
        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->
            if (placed) return@setOnTapArPlaneListener
            val anchor = hitResult.createAnchor()
            val anchorNode = com.google.ar.sceneform.AnchorNode(anchor).apply {
                setParent(arFragment.arSceneView.scene)
            }

            MaterialFactory.makeOpaqueWithColor(requireContext(), Color(android.graphics.Color.YELLOW))
                .thenAccept { material ->
                    val cube = ShapeFactory.makeCube(Vector3(0.08f, 0.08f, 0.08f), Vector3.zero(), material)
                    val node = TransformableNode(arFragment.transformationSystem).apply {
                        renderable = cube
                        setParent(anchorNode)
                        select()
                    }
                    setupCollect(node)
                }
            placed = true
        }

        binding.toInventoryButton.setOnClickListener {
            findNavController().navigate(R.id.action_arScannerFragment_to_inventoryFragment)
        }
    }

    private fun setupCollect(node: Node) {
        node.setOnTapListener { _, _ ->
            val artefact: Artefact = InMemoryGameRepository.defaultArtefact()
            gameViewModel.addArtefact(artefact)
            Toast.makeText(requireContext(), getString(R.string.artefact_collected), Toast.LENGTH_SHORT).show()
        }
    }
}
