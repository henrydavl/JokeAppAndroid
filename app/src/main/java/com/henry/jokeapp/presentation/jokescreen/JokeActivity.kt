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
import com.henry.core.entity.category.JokeCategory
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
        observeCategoryList()
    }

    private fun observeCategoryList() {
        viewModel.categoryList.observe(this) { response ->
            when (response) {
                is Resource.Loading -> {
                    isLoading(true)
                }

                is Resource.Success -> {
                    isLoading(false)
                    loadCategory(response.data)
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

    private fun loadCategory(category: JokeCategory?) = with(binding) {
        category?.let {
            jokeAdapter = ExpandableJokeAdapter(
                onCategoryClicked = { selectedCategory ->
                    viewModel.getJokeByCategory(selectedCategory)
                    observeJokeOnCategory()
                },
                onJokeClicked = { joke ->
                    Timber.tag(TAG).e("Joke clicked: ${joke.joke}")
                    showToastMessage("Joke clicked: ${joke.joke}")
                }
            )
            rvJokeCategory.apply {
                layoutManager = LinearLayoutManager(this@JokeActivity)
                adapter = jokeAdapter
            }
            jokeAdapter?.submitCategories(it.categories)
        }
    }


    private fun loadJoke(item: JokeItem?) = with(viewModel) {
        item?.let {
            Timber.tag(TAG).e("${it.jokes}")
            jokeList.value = it.jokes
            addModeCount += 1
            jokeAdapter?.expandCategoryExclusive(selectedCategory, it.jokes)
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