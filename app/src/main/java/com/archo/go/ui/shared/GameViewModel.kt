package com.archo.go.ui.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.archo.go.domain.model.Artefact
import com.archo.go.domain.model.QuizQuestion
import com.archo.go.domain.repository.GameRepository
import kotlinx.coroutines.flow.StateFlow

class GameViewModel(private val repository: GameRepository) : ViewModel() {
    val points: StateFlow<Int> = repository.points
    val inventory = repository.inventory

    fun addArtefact(artefact: Artefact) = repository.addArtefact(artefact)
    fun getQuizQuestions(): List<QuizQuestion> = repository.getQuizQuestions()
    fun addQuizPoints(points: Int) = repository.addQuizPoints(points)
}

class GameViewModelFactory(private val repository: GameRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return GameViewModel(repository) as T
    }
}
