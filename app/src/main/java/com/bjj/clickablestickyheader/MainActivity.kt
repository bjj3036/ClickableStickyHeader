package com.bjj.clickablestickyheader

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bjj.clickablestickyheader.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val adapter = SampleAdapter()
        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        val stickyHeaderItemDecorator = StickyHeaderItemDecorator<RecyclerView.ViewHolder>()
        stickyHeaderItemDecorator.attachRecyclerView(
            binding.headerContainer,
            binding.recyclerview,
            adapter,
            adapter
        )
        adapter.submitList(getTestItems())
    }
}

private fun getTestItems(): List<Item> {
    return buildList {
        repeat(100) {
            if (Math.random() > 0.1) {
                add(Item.Content("Content: $it"))
            } else {
                add(Item.Header("Header: $it"))
            }
        }
    }
}

