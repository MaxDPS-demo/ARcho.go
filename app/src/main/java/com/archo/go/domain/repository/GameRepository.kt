package com.archo.go.domain.repository

import com.archo.go.domain.model.ArchaeologicalSite
import com.archo.go.domain.model.Artefact
import com.archo.go.domain.model.QuizQuestion
import kotlinx.coroutines.flow.StateFlow

interface GameRepository {
    fun getSites(): List<ArchaeologicalSite>
    fun getSiteById(siteId: String): ArchaeologicalSite?

    val inventory: StateFlow<List<Artefact>>
    val points: StateFlow<Int>

    fun addArtefact(artefact: Artefact)
    fun getQuizQuestions(): List<QuizQuestion>
    fun addQuizPoints(points: Int)
}
