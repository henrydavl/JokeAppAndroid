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

    fun submitJokes(category: String, jokes: List<Joke>) {
        val index = items.indexOfFirst { it is JokeListItem.CategoryItem && it.category == category }
        if (index == -1) return

        // Don't add again if already expanded
        val isAlreadyExpanded = (items.getOrNull(index + 1) is JokeListItem.JokeItem)
        if (isAlreadyExpanded) return

        val jokeItems = jokes.map { JokeListItem.JokeItem(it) }.toMutableList<JokeListItem>()

        if (jokeItems.size < 6) {
            jokeItems.add(JokeListItem.LoadMoreItem(category))
        }

        items.addAll(index + 1, jokeItems)
        notifyItemRangeInserted(index + 1, jokeItems.size)

        // Update the expanded state
        val currentItem = items[index] as JokeListItem.CategoryItem
        items[index] = currentItem.copy(isExpanded = true)
        notifyItemChanged(index)
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