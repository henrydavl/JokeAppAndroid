package com.henry.jokeapp.presentation.jokescreen.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.henry.core.entity.jokeitem.Joke
import com.henry.jokeapp.databinding.ItemCategoryBinding
import com.henry.jokeapp.databinding.ItemJokeBinding
import com.henry.jokeapp.databinding.ItemLoadMoreBinding
import com.henry.jokeapp.presentation.jokescreen.adapter.ItemMenuViewType.VIEW_TYPE_CATEGORY
import com.henry.jokeapp.presentation.jokescreen.adapter.ItemMenuViewType.VIEW_TYPE_ITEM
import com.henry.jokeapp.presentation.jokescreen.adapter.ItemMenuViewType.VIEW_TYPE_LOAD_MORE
import com.henry.jokeapp.utils.JokeListItem

class ExpandableJokeAdapter(
    private val onCategoryClicked: (category: String) -> Unit,
    private val onJokeClicked: (joke: Joke) -> Unit,
    private val onLoadMoreClickListener: ((category: String, position: Int) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<JokeListItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            VIEW_TYPE_CATEGORY -> CategoryViewHolder(
                ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            VIEW_TYPE_ITEM -> JokeViewHolder(
                ItemJokeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            VIEW_TYPE_LOAD_MORE -> LoadMoreViewHolder(
                ItemLoadMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is JokeListItem.CategoryItem -> (holder as CategoryViewHolder).bind(item.category, position)
            is JokeListItem.JokeItem -> (holder as JokeViewHolder).bind(item.joke)
            is JokeListItem.LoadMoreItem -> (holder as LoadMoreViewHolder).bind(item.category)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is JokeListItem.CategoryItem -> VIEW_TYPE_CATEGORY
            is JokeListItem.JokeItem -> VIEW_TYPE_ITEM
            is JokeListItem.LoadMoreItem -> VIEW_TYPE_LOAD_MORE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitCategories(categories: List<String>) {
        items.clear()
        items.addAll(categories.map { JokeListItem.CategoryItem(it) })
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun expandCategoryExclusive(category: String, newJokes: List<Joke>) {
        val currentCategoryIndex = items.indexOfFirst {
            it is JokeListItem.CategoryItem && it.category == category && it.isExpanded
        }

        if (currentCategoryIndex != -1) {
            return
        }

        val newList = mutableListOf<JokeListItem>()

        items.forEach {
            if (it is JokeListItem.CategoryItem) {
                newList.add(
                    if (it.category == category) it.copy(isExpanded = true)
                    else it.copy(isExpanded = false)
                )

                if (it.category == category) {
                    newList.addAll(newJokes.take(2).map { joke -> JokeListItem.JokeItem(joke) })
                    if (newJokes.size < 6) {
                        newList.add(JokeListItem.LoadMoreItem(category))
                    }
                }
            }
        }

        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }


    inner class CategoryViewHolder(
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(category: String, position: Int) = with(binding) {
            tvCategoryNumber.text = (position + 1).toString()
            tvCategory.text = category
            root.setOnClickListener { onCategoryClicked(category) }
        }
    }

    inner class JokeViewHolder(private val binding: ItemJokeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(joke: Joke) = with(binding) {
            tvJokeContent.text = joke.joke
            root.setOnClickListener { onJokeClicked(joke) }
        }
    }

    inner class LoadMoreViewHolder(private val binding: ItemLoadMoreBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: String) {
            binding.btnLoadMore.setOnClickListener { onLoadMoreClickListener?.invoke(category, adapterPosition) }
        }
    }
}