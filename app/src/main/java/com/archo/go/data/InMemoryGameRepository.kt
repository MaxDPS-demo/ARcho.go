package com.archo.go.data

import com.archo.go.R
import com.archo.go.domain.model.ArchaeologicalSite
import com.archo.go.domain.model.Artefact
import com.archo.go.domain.model.QuizQuestion
import com.archo.go.domain.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryGameRepository : GameRepository {

    private val _inventory = MutableStateFlow<List<Artefact>>(emptyList())
    override val inventory: StateFlow<List<Artefact>> = _inventory.asStateFlow()

    private val _points = MutableStateFlow(0)
    override val points: StateFlow<Int> = _points.asStateFlow()

    private val sites = listOf(
        ArchaeologicalSite("s1", "Pustý hrad", "Rozsiahla zrúcanina hradu nad Zvolenom.", 48.5795, 19.1462),
        ArchaeologicalSite("s2", "Stará radnica Zvolen", "Historické centrum so stopami stredovekého osídlenia.", 48.5764, 19.1256),
        ArchaeologicalSite("s3", "Rímskokatolícky kostol Dobrá Niva", "Sakrálny objekt s historickými artefaktmi.", 48.3982, 19.1103),
        ArchaeologicalSite("s4", "Petuša", "Archeologické hradisko pri sútoku Hrona a Slatiny.", 48.5751, 19.1805),
        ArchaeologicalSite("s5", "Šášov", "Hradná lokalita s nálezmi z neskorého stredoveku.", 48.5454, 18.8407),
        ArchaeologicalSite("s6", "Revište", "Zrúcanina hradu Revište s bohatou históriou.", 48.4639, 18.9182)
    )

    override fun getSites(): List<ArchaeologicalSite> = sites

    override fun getSiteById(siteId: String): ArchaeologicalSite? = sites.firstOrNull { it.id == siteId }

    override fun addArtefact(artefact: Artefact) {
        if (_inventory.value.any { it.id == artefact.id }) return
        _inventory.value = _inventory.value + artefact
        _points.value += artefact.points
    }

    override fun getQuizQuestions(): List<QuizQuestion> {
        val collectedNames = _inventory.value.map { it.name }
        if (collectedNames.isEmpty()) return emptyList()

        return collectedNames.shuffled().take(3).map { artefactName ->
            val wrong = listOf("Minca z bronzu", "Hlinená nádoba", "Rímsky meč", "Kostenná ihla")
                .filter { it != artefactName }
                .shuffled()
                .take(3)
            val answers = (wrong + artefactName).shuffled()
            QuizQuestion(
                question = "Ktorý artefakt si už získal v AR?",
                answers = answers,
                correctIndex = answers.indexOf(artefactName)
            )
        }
    }

    override fun addQuizPoints(points: Int) {
        _points.value += points
    }

    companion object {
        fun defaultArtefact(): Artefact = Artefact(
            id = "coin_1",
            name = "Rímska minca",
            iconRes = R.drawable.ic_artefact,
            points = 50
        )
    }
}
