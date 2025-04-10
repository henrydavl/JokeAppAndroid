package com.henry.jokeapp.presentation.jokescreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henry.api.joke.domain.get.GetCategoriesUseCase
import com.henry.api.joke.domain.get.GetJokeByCategoryUseCase
import com.henry.core.entity.category.JokeCategory
import com.henry.core.entity.jokeitem.Joke
import com.henry.core.entity.jokeitem.JokeItem
import com.henry.core.utils.Resource
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

    private val _categoryList = MutableLiveData<Resource<JokeCategory>>()
    val categoryList get() = _categoryList

    private val _jokesOnCategory = MutableLiveData<Resource<JokeItem>>()
    val jokesOnCategory get() = _jokesOnCategory

    internal var jokeList = MutableLiveData<List<Joke>>()
    internal var addModeCount = 0
    internal var selectedCategory = ""

    fun getCategories() = viewModelScope.launch {
        getCategoriesUseCase.invoke().collect { _categoryList.postValue(it) }
    }

    fun getJokeByCategory(category: String) = viewModelScope.launch {
        selectedCategory = category
        getJokeByCategoryUseCase.invoke(category).collect {
            _jokesOnCategory.postValue(it)
        }
    }
}