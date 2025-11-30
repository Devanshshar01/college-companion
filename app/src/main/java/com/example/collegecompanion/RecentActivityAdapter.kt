package com.example.collegecompanion

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.collegecompanion.databinding.ItemActivityBinding

/**
 * Adapter for displaying recent activity items in RecyclerView.
 * Uses DiffUtil for efficient list updates and smooth animations.
 */
class RecentActivityAdapter(private var items: List<ActivityItem>) :
    RecyclerView.Adapter<RecentActivityAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemActivityBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemActivityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            title.text = item.title
            subtitle.text = item.subtitle
            time.text = item.time

            // Set icon based on type
            val iconRes = when (item.icon) {
                "reminder" -> R.drawable.ic_list
                "class" -> R.drawable.ic_class
                else -> R.drawable.ic_calendar
            }
            icon.setImageResource(iconRes)
        }
    }

    override fun getItemCount() = items.size

    /**
     * Update items with DiffUtil for smooth animations.
     */
    fun updateItems(newItems: List<ActivityItem>) {
        val diffCallback = ActivityDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    /**
     * DiffUtil callback for efficient list updates.
     */
    private class ActivityDiffCallback(
        private val oldList: List<ActivityItem>,
        private val newList: List<ActivityItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].title == newList[newItemPosition].title &&
                    oldList[oldItemPosition].time == newList[newItemPosition].time
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
