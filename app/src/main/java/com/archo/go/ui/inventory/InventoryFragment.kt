package com.archo.go.ui.inventory

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import com.archo.go.R
import com.archo.go.data.ServiceLocator
import com.archo.go.databinding.FragmentInventoryBinding
import com.archo.go.ui.shared.GameViewModel
import com.archo.go.ui.shared.GameViewModelFactory
import kotlinx.coroutines.launch

class InventoryFragment : Fragment(R.layout.fragment_inventory) {

    private val gameViewModel: GameViewModel by activityViewModels {
        GameViewModelFactory(ServiceLocator.repository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentInventoryBinding.bind(view)
        val adapter = InventoryAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            gameViewModel.inventory.collect { items ->
                adapter.submitList(items)
                binding.emptyText.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        binding.toQuizButton.setOnClickListener {
            findNavController().navigate(R.id.action_inventoryFragment_to_quizFragment)
        }
    }
}
