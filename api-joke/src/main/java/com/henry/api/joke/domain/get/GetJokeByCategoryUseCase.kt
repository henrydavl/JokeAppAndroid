package com.henry.api.joke.domain.get

import com.henry.api.joke.data.repository.JokeRepository
import javax.inject.Inject

class GetJokeByCategoryUseCase @Inject constructor(
    private val repository: JokeRepository
) {
    operator fun invoke(category: String) = repository.getJokeByCategory(category)
}