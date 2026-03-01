package com.archo.go.domain.model

data class QuizQuestion(
    val question: String,
    val answers: List<String>,
    val correctIndex: Int
)
