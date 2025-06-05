package com.bjj.clickablestickyheader

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bjj.clickablestickyheader.databinding.ItemContentBinding
import com.bjj.clickablestickyheader.databinding.ItemHeaderBinding

private val diffCallback = object : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean = oldItem == newItem
}

class SampleAdapter : ListAdapter<Item, RecyclerView.ViewHolder>(diffCallback),
    StickyHeaderItemDecorator.StickyHeaderInterface {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> HeaderViewHolder(ItemHeaderBinding.inflate(layoutInflater, parent, false).root)
            1 -> ContentViewHolder(ItemContentBinding.inflate(layoutInflater, parent, false).root)
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val text = getItem(position).text
        when (holder) {
            is HeaderViewHolder -> {
                holder.bind(text)
                holder.itemView.setOnClickListener {
                    Toast.makeText(holder.itemView.context, "$text clicked", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            is ContentViewHolder -> holder.bind(text)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Item.Header -> 0
            is Item.Content -> 1
        }
    }

    override fun isHeader(itemPosition: Int): Boolean {
        if (itemPosition < 0 || itemPosition >= itemCount) return false
        return getItem(itemPosition) is Item.Header
    }
}

class HeaderViewHolder(private val view: TextView) : RecyclerView.ViewHolder(view) {
    fun bind(text: String) {
        view.text = text
    }
}

class ContentViewHolder(private val view: TextView) : RecyclerView.ViewHolder(view) {
    fun bind(text: String) {
        view.text = text
    }
}