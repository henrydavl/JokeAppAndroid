package com.henry.jokeapp.presentation.jokescreen

import android.graphics.Color
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.henry.core.entity.jokeitem.JokeItem
import com.henry.core.utils.Resource
import com.henry.jokeapp.databinding.ActivityJokeBinding
import com.henry.jokeapp.presentation.jokescreen.adapter.ExpandableJokeAdapter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class JokeActivity : AppCompatActivity() {
    companion object {
        const val TAG = "JokeActivity"
    }

    private val viewModel: JokeActivityViewModel by viewModels()

    private lateinit var binding: ActivityJokeBinding
    private var jokeAdapter: ExpandableJokeAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)
        binding = ActivityJokeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getCategories()
        observeCategory()
    }

    private fun observeCategory() {
        viewModel.categoryList.observe(this) { response ->
            when (response) {
                is Resource.Loading -> {
                    isLoading(true)
                }

                is Resource.Success -> {
                    isLoading(false)
                    viewModel.setCategoryNameList(response.data?.categories)
                    observeCategoryList()
                    Timber.tag(TAG).e("${response.data?.categories}")
                }

                is Resource.Error -> {
                    isLoading(false)
                    response.message?.let {
                        Timber.tag(TAG).e(it)
                        showToastMessage("An error occurred $it")
                    }
                }
            }
        }
    }

    private fun observeJokeOnCategory() {
        viewModel.jokesOnCategory.observe(this) { response ->
            when (response) {
                is Resource.Loading -> {

                }

                is Resource.Success -> {
                    loadJoke(response.data)
                }

                is Resource.Error -> {
                    response.message?.let {
                        Timber.tag(TAG).e(it)
                        showToastMessage("An error occurred $it")
                    }
                }
            }
        }
    }

    private fun observeCategoryList() {
        viewModel.categoryNameList.observe(this) { categories ->
            Timber.tag(TAG).e("Category: $categories")
            categories?.let { loadCategory(categories) }
        }
    }

    private fun loadCategory(categories: List<Pair<Int, String>>) = with(binding) {
        jokeAdapter = ExpandableJokeAdapter(
            onCategoryClicked = { selectedCategory ->
                viewModel.getJokeByCategory(selectedCategory, false)
                observeJokeOnCategory()
            },
            onJokeClicked = { joke ->
                Timber.tag(TAG).e("Joke clicked: ${joke.joke}")
                showToastMessage("Joke clicked: ${joke.joke}")
            },
            onPinTopPressed = { category ->
                viewModel.moveItemToTop(category)
            },
            onLoadMoreClickListener = { category ->
                viewModel.getJokeByCategory(category, isLoadMore = true)
                observeJokeOnCategory()
            }
        )
        rvJokeCategory.apply {
            layoutManager = LinearLayoutManager(this@JokeActivity)
            adapter = jokeAdapter
        }
        jokeAdapter?.submitCategories(categories)
        rvJokeCategory.smoothScrollToPosition(0)
    }

    private fun loadJoke(item: JokeItem?) = with(viewModel) {
        item?.let {
            Timber.tag(TAG).e("${it.jokes} | ${it.jokes.map { it.category }}")
            if (isLoadMore) {
                jokeList += it.jokes
                Timber.e("$TAG Joke list: ${jokeList.size}")
                isLoadMore = false
                jokeAdapter?.appendMoreJokes(selectedCategory, it.jokes)
            } else {
                jokeList = it.jokes.toMutableList()
                Timber.e("$TAG Joke list: ${jokeList.size}")
                jokeAdapter?.expandCategoryExclusive(selectedCategory, jokeList)
            }
        }
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isLoading(isLoading: Boolean) = with(binding) {
        if (isLoading) {
            pbLoading.visibility = VISIBLE
            rvJokeCategory.visibility = GONE
        } else {
            pbLoading.visibility = GONE
            rvJokeCategory.visibility = VISIBLE
        }
    }
}