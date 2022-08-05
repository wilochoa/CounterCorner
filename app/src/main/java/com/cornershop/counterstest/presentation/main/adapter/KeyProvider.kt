package com.cornershop.counterstest.presentation.main.adapter

import androidx.recyclerview.selection.ItemKeyProvider

class KeyProvider(private val adapter: MainAdapter) : ItemKeyProvider<String>(SCOPE_CACHED) {
    override fun getKey(position: Int): String? = adapter.currentList[position].id
    override fun getPosition(key: String): Int =
        adapter.currentList.indexOfFirst { it.id == key }
}