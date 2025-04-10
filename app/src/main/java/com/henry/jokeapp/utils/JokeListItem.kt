package com.henry.jokeapp.utils

import com.henry.core.entity.jokeitem.Joke

sealed class JokeListItem {
    data class CategoryItem(val category: String, val isExpanded: Boolean = false) : JokeListItem()
    data class JokeItem(val joke: Joke) : JokeListItem()
    data class LoadMoreItem(val category: String) : JokeListItem()
}
