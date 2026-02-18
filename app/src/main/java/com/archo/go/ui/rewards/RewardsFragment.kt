package com.archo.go.ui.rewards

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.archo.go.R
import com.archo.go.data.ServiceLocator
import com.archo.go.databinding.FragmentRewardsBinding
import com.archo.go.ui.shared.GameViewModel
import com.archo.go.ui.shared.GameViewModelFactory
import kotlinx.coroutines.launch

class RewardsFragment : Fragment(R.layout.fragment_rewards) {

    private val gameViewModel: GameViewModel by activityViewModels {
        GameViewModelFactory(ServiceLocator.repository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentRewardsBinding.bind(view)

        val quizPercent = arguments?.getInt("quizPercent") ?: 0
        val quizPoints = arguments?.getInt("quizPoints") ?: 0
        binding.quizResult.text = getString(R.string.quiz_result_format, quizPercent, quizPoints)

        viewLifecycleOwner.lifecycleScope.launch {
            gameViewModel.points.collect { points ->
                binding.pointsValue.text = getString(R.string.points_value, points)
            }
        }

        binding.exchangeMeetup.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.exchange_meetup_done), Toast.LENGTH_SHORT).show()
        }

        binding.exchangePrint.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.exchange_print_done), Toast.LENGTH_SHORT).show()
        }

        binding.backToMap.setOnClickListener {
            findNavController().navigate(R.id.action_rewardsFragment_to_mapFragment)
        }
    }
}
