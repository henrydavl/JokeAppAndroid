package com.henry.jokeapp.presentation.jokescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henry.api.joke.domain.get.GetCategoriesUseCase
import com.henry.api.joke.domain.get.GetJokeByCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class JokeActivityViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getJokeByCategoryUseCase: GetJokeByCategoryUseCase,
) : ViewModel() {

    companion object {
        const val TAG = "JokeActivityViewModel"
    }

    fun getCategories() = viewModelScope.launch {
        getCategoriesUseCase.invoke().collect {
            Timber.tag(TAG).e("${it.data?.categories}")
        }
    }

    fun getJokeByCategory(category: String = "Any") = viewModelScope.launch {
        getJokeByCategoryUseCase.invoke(category).collect {
            Timber.tag(TAG).e("${it.data?.jokes}")
        }
    }
}