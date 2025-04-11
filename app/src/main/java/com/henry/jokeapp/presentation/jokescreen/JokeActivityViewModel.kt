package com.henry.jokeapp.presentation.jokescreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henry.api.joke.domain.get.GetCategoriesUseCase
import com.henry.api.joke.domain.get.GetJokeByCategoryUseCase
import com.henry.core.entity.category.JokeCategory
import com.henry.core.entity.jokeitem.Joke
import com.henry.core.entity.jokeitem.JokeItem
import com.henry.core.utils.Resource
import com.henry.jokeapp.utils.JokeListItem
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
    val categoryList: LiveData<Resource<JokeCategory>> get() = _categoryList

    private val _categoryNameList = MutableLiveData<List<Pair<Int, String>>?>()
    val categoryNameList: LiveData<List<Pair<Int, String>>?> get() = _categoryNameList

    private val _jokesOnCategory = MutableLiveData<Resource<JokeItem>>()
    val jokesOnCategory get() = _jokesOnCategory

    internal var jokeList = MutableLiveData<List<Joke>>()
    internal var addModeCount = 0
    internal var selectedCategory: Pair<Int, String> = Pair(0, "")

    fun getCategories() = viewModelScope.launch {
        getCategoriesUseCase.invoke().collect { _categoryList.postValue(it) }
    }

    fun getJokeByCategory(category: Pair<Int, String>) = viewModelScope.launch {
        Timber.tag(TAG).e("getJokeByCategory: $category")
        selectedCategory = category
        getJokeByCategoryUseCase.invoke(category.second).collect {
            _jokesOnCategory.postValue(it)
        }
    }

    fun setCategoryNameList(categoryNameList: List<String>?) {
        _categoryNameList.postValue(
            categoryNameList?.mapIndexed { index, value -> index to value }
        )
    }

    fun moveItemToTop(category: JokeListItem.CategoryItem) {
        val currentList = _categoryNameList.value?.toMutableList() ?: return
        currentList.remove(category.category)
        currentList.add(0, category.category)
        _categoryNameList.value = currentList
    }
}