package com.henry.api.joke.util

import com.henry.api.joke.util.Path.CATEGORY

object JokeUrlConstants {
    const val GET_CATEGORIES = "/categories"
    const val GET_JOKE = "/joke/{category}"
}

object Path {
    const val CATEGORY = "category"
}

object Query {
    const val TYPE = "type"
    const val AMOUNT = "amount"
}