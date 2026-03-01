package com.archo.go.data

import com.archo.go.domain.repository.GameRepository

object ServiceLocator {
    val repository: GameRepository by lazy { InMemoryGameRepository() }
}
