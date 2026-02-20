package com.archo.go.ui.quiz

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.archo.go.R
import com.archo.go.data.ServiceLocator
import com.archo.go.databinding.FragmentQuizBinding
import com.archo.go.domain.model.QuizQuestion
import com.archo.go.ui.shared.GameViewModel
import com.archo.go.ui.shared.GameViewModelFactory

class QuizFragment : Fragment(R.layout.fragment_quiz) {

    private val gameViewModel: GameViewModel by activityViewModels {
        GameViewModelFactory(ServiceLocator.repository)
    }

    private var questions: List<QuizQuestion> = emptyList()
    private var index = 0
    private var correct = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentQuizBinding.bind(view)

        questions = gameViewModel.getQuizQuestions()
        if (questions.isEmpty()) {
            binding.question.text = getString(R.string.quiz_no_data)
            binding.submitButton.isEnabled = false
            return
        }

        showQuestion(binding)

        binding.submitButton.setOnClickListener {
            val selectedIndex = selectedIndex(binding)
            if (selectedIndex == -1) {
                Toast.makeText(requireContext(), getString(R.string.select_answer_first), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedIndex == questions[index].correctIndex) correct++
            index++

            if (index >= questions.size) {
                val percent = (correct * 100) / questions.size
                val earnedPoints = correct * 20
                gameViewModel.addQuizPoints(earnedPoints)

                findNavController().navigate(
                    R.id.action_quizFragment_to_rewardsFragment,
                    Bundle().apply {
                        putInt("quizPercent", percent)
                        putInt("quizPoints", earnedPoints)
                    }
                )
            } else {
                binding.answersGroup.clearCheck()
                showQuestion(binding)
            }
        }
    }

    private fun selectedIndex(binding: FragmentQuizBinding): Int {
        val checkedId = binding.answersGroup.checkedRadioButtonId
        val ids = listOf(binding.answerA.id, binding.answerB.id, binding.answerC.id, binding.answerD.id)
        return ids.indexOf(checkedId)
    }

    private fun showQuestion(binding: FragmentQuizBinding) {
        val q = questions[index]
        binding.question.text = q.question
        listOf(binding.answerA, binding.answerB, binding.answerC, binding.answerD).forEachIndexed { i, radio ->
            radio.text = q.answers.getOrNull(i).orEmpty()
        }
        binding.progress.text = getString(R.string.quiz_progress, index + 1, questions.size)
    }
}
