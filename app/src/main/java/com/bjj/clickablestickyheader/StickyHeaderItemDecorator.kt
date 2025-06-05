package com.bjj.clickablestickyheader

import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StickyHeaderItemDecorator<VH : RecyclerView.ViewHolder> {

    private lateinit var listener: StickyHeaderInterface
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerView.Adapter<VH>
    private lateinit var stickyHeaderContainer: ViewGroup

    fun attachRecyclerView(
        stickyHeaderContainer: ViewGroup,
        recyclerView: RecyclerView,
        listener: StickyHeaderInterface,
        adapter: RecyclerView.Adapter<VH>
    ) {
        this.stickyHeaderContainer = stickyHeaderContainer
        this.listener = listener
        this.recyclerView = recyclerView
        this.adapter = adapter
        initContainer()
        clearHeaderViews()
        redrawHeader()
    }

    private fun initContainer() {
        recyclerView.addOnScrollListener(onScrollChangeListener)
    }

    private fun clearHeaderViews() {
        stickyHeaderContainer.removeAllViews()
    }

    private fun addHeaderViewFromPosition(position: Int): View {
        stickyHeaderContainer.let {
            val viewType = adapter.getItemViewType(position)
            val vh = adapter.createViewHolder(it, viewType)
            adapter.bindViewHolder(vh, position)
            val view = vh.itemView
            view.setTag(R.id.tag_position, position)
            view.setTag(R.id.tag_viewholder, vh)
            view.setTag(R.id.tag_viewtype, viewType)
            it.addView(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            recyclerView.post { it.requestLayout() }
            return view
        }
    }

    private fun drawHeaders(refresh: Boolean) {
        val stickyHeaderContainer = stickyHeaderContainer
        val headerPosition = findHeaderPosition() ?: run {
            stickyHeaderContainer.let {
                if (it.isNotEmpty()) it.removeAllViews()
            }
            return
        }
        val headerViewType = adapter.getItemViewType(headerPosition)
        var exist = false
        var headerView: View? = null
        for (i in 0 until stickyHeaderContainer.childCount) {
            val child = stickyHeaderContainer.getChildAt(i) ?: continue
            if (child.getTag(R.id.tag_position) == headerPosition && child.getTag(R.id.tag_viewtype) == headerViewType) {
                headerView = child
                if (refresh) {
                    adapter.onBindViewHolder(
                        child.getTag(R.id.tag_viewholder) as VH,
                        headerPosition
                    )
                }
                exist = true
            } else {
                stickyHeaderContainer.removeView(child)
            }
        }
        if (exist.not()) {
            headerView = addHeaderViewFromPosition(headerPosition)
        }
        if (headerView == null) return
        val nextHeaderPosition = findNextHeaderPosition() ?: run {
            headerView.translationY = 0f
            return
        }
        recyclerView.children.forEach { child ->
            val viewHolder = recyclerView.getChildViewHolder(child)
            if (viewHolder.bindingAdapterPosition == nextHeaderPosition) {
                headerView.measure(
                    MeasureSpec.makeMeasureSpec(
                        stickyHeaderContainer.width,
                        MeasureSpec.EXACTLY
                    ),
                    MeasureSpec.makeMeasureSpec(stickyHeaderContainer.height, MeasureSpec.AT_MOST)
                )
                val top = child.top
                headerView.translationY =
                    (top - headerView.measuredHeight).coerceAtMost(0).toFloat()
            }
        }
    }

    private val onScrollChangeListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                redrawHeader()
            }
        }

    fun redrawHeader(refresh: Boolean = false) {
        recyclerView.post {
            drawHeaders(refresh)
        }
    }

    fun clearReferences() {
        recyclerView.removeOnScrollListener(onScrollChangeListener)
    }

    private fun findHeaderPosition(): Int? {
        val firstVisibleIndex =
            (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        if (firstVisibleIndex == RecyclerView.NO_POSITION) return null
        for (i in firstVisibleIndex downTo 0) {
            if (listener.isHeader(i)) return i
        }
        return null
    }

    private fun findNextHeaderPosition(): Int? {
        val layoutManager = (recyclerView.layoutManager as LinearLayoutManager)
        val firstVisibleIndex = layoutManager.findFirstVisibleItemPosition()
        val lastVisibleIndex = layoutManager.findLastVisibleItemPosition()
        if (firstVisibleIndex == RecyclerView.NO_POSITION || lastVisibleIndex == RecyclerView.NO_POSITION) return null
        for (i in (firstVisibleIndex + 1)..lastVisibleIndex) {
            if (listener.isHeader(i)) return i
        }
        return null
    }

    interface StickyHeaderInterface {
        fun isHeader(itemPosition: Int): Boolean
    }
}