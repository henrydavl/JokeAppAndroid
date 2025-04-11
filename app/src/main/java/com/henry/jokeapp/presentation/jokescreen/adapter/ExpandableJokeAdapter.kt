package com.henry.jokeapp.presentation.jokescreen.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.henry.core.entity.jokeitem.Joke
import com.henry.jokeapp.R
import com.henry.jokeapp.databinding.ItemCategoryBinding
import com.henry.jokeapp.databinding.ItemJokeBinding
import com.henry.jokeapp.databinding.ItemLoadMoreBinding
import com.henry.jokeapp.presentation.jokescreen.adapter.ItemMenuViewType.VIEW_TYPE_CATEGORY
import com.henry.jokeapp.presentation.jokescreen.adapter.ItemMenuViewType.VIEW_TYPE_ITEM
import com.henry.jokeapp.presentation.jokescreen.adapter.ItemMenuViewType.VIEW_TYPE_LOAD_MORE
import com.henry.jokeapp.utils.JokeListItem

class ExpandableJokeAdapter(
    private val onCategoryClicked: (category: Pair<Int, String>) -> Unit,
    private val onJokeClicked: (joke: Joke) -> Unit,
    private val onPinTopPressed: ((category: JokeListItem.CategoryItem) -> Unit)? = null,
    private val onLoadMoreClickListener: ((category: Pair<Int, String>) -> Unit)? = null
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
            is JokeListItem.CategoryItem -> (holder as CategoryViewHolder).bind(item, position)
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
    fun submitCategories(categories: List<Pair<Int, String>>) {
        items.clear()
        items.addAll(
            categories.map { category -> JokeListItem.CategoryItem(category = category) }
        )
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun expandCategoryExclusive(category: Pair<Int, String>, newJokes: List<Joke>) {
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
                    if (it.category == category) it.apply { isExpanded = true }
                    else it.apply { isExpanded = false }
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

    @SuppressLint("NotifyDataSetChanged")
    fun appendMoreJokes(category: Pair<Int, String>, newJokes: List<Joke>) {
        val categoryIndex = items.indexOfFirst {
            it is JokeListItem.CategoryItem && it.category == category
        }

        if (categoryIndex == -1) return

        val loadMoreIndex = items.indexOfFirst {
            it is JokeListItem.LoadMoreItem && it.category == category
        }
        if (loadMoreIndex != -1) {
            items.removeAt(loadMoreIndex)
            notifyItemRemoved(loadMoreIndex)
        }

        val jokeItems = newJokes.map { JokeListItem.JokeItem(it) }
        val insertPosition = loadMoreIndex.takeIf { it != -1 } ?: (categoryIndex + 1)

        items.addAll(insertPosition, jokeItems)
        notifyItemRangeInserted(insertPosition, jokeItems.size)

        val totalJokesShown = items.count {
            it is JokeListItem.JokeItem && items.indexOf(it) > categoryIndex
        }
        if (totalJokesShown < 6) {
            items.add(insertPosition + jokeItems.size, JokeListItem.LoadMoreItem(category))
            notifyItemInserted(insertPosition + jokeItems.size)
        }
    }

    inner class CategoryViewHolder(
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(category: JokeListItem.CategoryItem, position: Int) = with(binding) {
            tvCategoryNumber.text = (category.category.first + 1).toString()
            tvCategory.text = category.category.second

            if (position == 0) {
                btnPinToTop.text = root.context.getText(R.string.pinned_on_top)
                btnPinToTop.isEnabled = false
            }
            else {
                btnPinToTop.text = root.context.getText(R.string.pin_to_top)
                btnPinToTop.isEnabled = true
            }

            if (position != 0) {
                btnPinToTop.setOnClickListener { onPinTopPressed?.invoke(category) }
            } else {
                btnPinToTop.setOnClickListener(null)
            }

            root.setOnClickListener { onCategoryClicked(category.category) }
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
        fun bind(category: Pair<Int, String>) {
            binding.btnLoadMore.setOnClickListener { onLoadMoreClickListener?.invoke(category) }
        }
    }
}