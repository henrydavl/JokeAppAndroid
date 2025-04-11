package com.henry.jokeapp.utils

import com.henry.core.entity.jokeitem.Joke

sealed class JokeListItem {
    data class CategoryItem(val category: Pair<Int, String>, var isExpanded: Boolean = false) : JokeListItem()
    data class JokeItem(val joke: Joke) : JokeListItem()
    data class LoadMoreItem(val category: Pair<Int, String>) : JokeListItem()
}
